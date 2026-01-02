package com.gndy.camman.data.repository

import com.gndy.camman.data.chat.ChatWebSocketService
import com.gndy.camman.data.chat.ConnectionState
import com.gndy.camman.domain.model.AuthUser
import com.gndy.camman.domain.model.ChatParticipant
import com.gndy.camman.domain.model.ChatUserType
import com.gndy.camman.domain.model.Conversation
import com.gndy.camman.domain.model.Message
import com.gndy.camman.domain.model.MessageType
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.domain.repository.ChatRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Real-time implementation of ChatRepository using WebSocket
 * 
 * Now uses Supabase authentication for proper user identification
 */
class ChatRepositoryImpl(
    private val webSocketService: ChatWebSocketService,
    private val authRepository: AuthRepository
) : ChatRepository {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // In-memory storage (would be backed by local database in production)
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    
    // Connection state exposed to UI
    val connectionState: StateFlow<ConnectionState> = webSocketService.connectionState
    
    // Typing indicators per conversation
    private val _typingUsers = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val typingUsers: StateFlow<Map<String, Set<String>>> = _typingUsers.asStateFlow()
    
    /**
     * Get the current authenticated user ID
     * Falls back to "guest_user" when not logged in
     */
    val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "guest_user"
    
    /**
     * Get the current authenticated user name
     * Falls back to "Guest" when not logged in
     */
    val currentUserName: String
        get() = authRepository.currentUser?.displayName ?: authRepository.currentUser?.email?.substringBefore("@") ?: "Guest"
    
    /**
     * Get the current authenticated user photo URL
     */
    val currentUserPhotoUrl: String?
        get() = authRepository.currentUser?.photoUrl
    
    init {
        // Initialize with mock data
        initMockData()
        
        // Connect to WebSocket and listen for real-time messages
        scope.launch {
            // Connect to chat server
            webSocketService.connect(
                userId = currentUserId,
                userName = currentUserName
            )
            
            // Listen for incoming messages
            webSocketService.incomingMessages.collect { message ->
                addMessageToConversation(message)
            }
        }
        
        // Listen for typing indicators
        scope.launch {
            webSocketService.typingIndicators.collect { indicator ->
                updateTypingIndicator(indicator.conversationId, indicator.userId, indicator.isTyping)
            }
        }
        
        // Listen for user presence updates
        scope.launch {
            webSocketService.userPresence.collect { presence ->
                updateUserPresence(presence.userId, presence.isOnline)
            }
        }
        
        // Listen for read receipts
        scope.launch {
            webSocketService.messageReadReceipts.collect { receipt ->
                markMessageAsReadLocally(receipt.conversationId, receipt.messageId)
            }
        }
    }
    
    private fun initMockData() {
        val now = Clock.System.now()
        
        val mockConversations = listOf(
            Conversation(
                id = "conv_1",
                participantIds = listOf(currentUserId, "photographer_1"),
                participants = listOf(
                    ChatParticipant(
                        id = currentUserId,
                        name = currentUserName,
                        userType = ChatUserType.CLIENT,
                        isOnline = true
                    ),
                    ChatParticipant(
                        id = "photographer_1",
                        name = "Alex Rivera",
                        imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                        userType = ChatUserType.PHOTOGRAPHER,
                        isOnline = true,
                        lastSeenAt = now
                    )
                ),
                lastMessage = Message(
                    id = "msg_1_3",
                    conversationId = "conv_1",
                    senderId = "photographer_1",
                    senderName = "Alex Rivera",
                    content = "I'm available on that date! Would you like to proceed with booking?",
                    timestamp = now - 30.minutes,
                    isRead = false
                ),
                unreadCount = 1,
                updatedAt = now - 30.minutes
            ),
            Conversation(
                id = "conv_2",
                participantIds = listOf(currentUserId, "photographer_2"),
                participants = listOf(
                    ChatParticipant(
                        id = currentUserId,
                        name = currentUserName,
                        userType = ChatUserType.CLIENT,
                        isOnline = true
                    ),
                    ChatParticipant(
                        id = "photographer_2",
                        name = "Sarah Chen",
                        imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                        userType = ChatUserType.PHOTOGRAPHER,
                        isOnline = false,
                        lastSeenAt = now - 2.hours
                    )
                ),
                lastMessage = Message(
                    id = "msg_2_2",
                    conversationId = "conv_2",
                    senderId = currentUserId,
                    senderName = currentUserName,
                    content = "Thank you! I'll review the packages.",
                    timestamp = now - 2.hours,
                    isRead = true
                ),
                unreadCount = 0,
                updatedAt = now - 2.hours
            ),
            Conversation(
                id = "conv_3",
                participantIds = listOf(currentUserId, "photographer_3"),
                participants = listOf(
                    ChatParticipant(
                        id = currentUserId,
                        name = currentUserName,
                        userType = ChatUserType.CLIENT,
                        isOnline = true
                    ),
                    ChatParticipant(
                        id = "photographer_3",
                        name = "Marcus Johnson",
                        imageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400",
                        userType = ChatUserType.PHOTOGRAPHER,
                        isOnline = true,
                        lastSeenAt = now
                    )
                ),
                lastMessage = Message(
                    id = "msg_3_1",
                    conversationId = "conv_3",
                    senderId = "photographer_3",
                    senderName = "Marcus Johnson",
                    content = "Hi! Thanks for your interest. How can I help you today?",
                    timestamp = now - 5.hours,
                    isRead = true
                ),
                unreadCount = 0,
                updatedAt = now - 5.hours
            )
        )
        
        val mockMessages = mapOf(
            "conv_1" to listOf(
                Message(
                    id = "msg_1_1",
                    conversationId = "conv_1",
                    senderId = currentUserId,
                    senderName = currentUserName,
                    content = "Hi Alex! I love your wedding photography. Are you available for December 15th?",
                    timestamp = now - 1.hours,
                    isRead = true
                ),
                Message(
                    id = "msg_1_2",
                    conversationId = "conv_1",
                    senderId = "photographer_1",
                    senderName = "Alex Rivera",
                    senderImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                    content = "Thank you so much! Let me check my calendar for that date.",
                    timestamp = now - 45.minutes,
                    isRead = true
                ),
                Message(
                    id = "msg_1_3",
                    conversationId = "conv_1",
                    senderId = "photographer_1",
                    senderName = "Alex Rivera",
                    senderImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                    content = "I'm available on that date! Would you like to proceed with booking?",
                    timestamp = now - 30.minutes,
                    isRead = false
                )
            ),
            "conv_2" to listOf(
                Message(
                    id = "msg_2_1",
                    conversationId = "conv_2",
                    senderId = "photographer_2",
                    senderName = "Sarah Chen",
                    senderImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                    content = "Hi there! I'd be happy to discuss your portrait session. What style are you looking for?",
                    timestamp = now - 3.hours,
                    isRead = true
                ),
                Message(
                    id = "msg_2_2",
                    conversationId = "conv_2",
                    senderId = currentUserId,
                    senderName = currentUserName,
                    content = "Thank you! I'll review the packages.",
                    timestamp = now - 2.hours,
                    isRead = true
                )
            ),
            "conv_3" to listOf(
                Message(
                    id = "msg_3_1",
                    conversationId = "conv_3",
                    senderId = "photographer_3",
                    senderName = "Marcus Johnson",
                    senderImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400",
                    content = "Hi! Thanks for your interest. How can I help you today?",
                    timestamp = now - 5.hours,
                    isRead = true
                )
            )
        )
        
        _conversations.value = mockConversations
        _messages.value = mockMessages
    }
    
    /**
     * Add a new message to a conversation (called when receiving from WebSocket)
     */
    private fun addMessageToConversation(message: Message) {
        // Add to messages
        val currentMessages = _messages.value[message.conversationId] ?: emptyList()
        _messages.value = _messages.value + (message.conversationId to (currentMessages + message))
        
        // Update conversation's last message and unread count
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == message.conversationId) {
                val newUnreadCount = if (message.senderId != currentUserId) {
                    conv.unreadCount + 1
                } else {
                    conv.unreadCount
                }
                conv.copy(
                    lastMessage = message,
                    updatedAt = message.timestamp,
                    unreadCount = newUnreadCount
                )
            } else conv
        }
    }
    
    /**
     * Update typing indicator for a user in a conversation
     */
    private fun updateTypingIndicator(conversationId: String, userId: String, isTyping: Boolean) {
        val current = _typingUsers.value[conversationId] ?: emptySet()
        val updated = if (isTyping) {
            current + userId
        } else {
            current - userId
        }
        _typingUsers.value = _typingUsers.value + (conversationId to updated)
    }
    
    /**
     * Update user presence status
     */
    private fun updateUserPresence(userId: String, isOnline: Boolean) {
        _conversations.value = _conversations.value.map { conv ->
            conv.copy(
                participants = conv.participants.map { participant ->
                    if (participant.id == userId) {
                        participant.copy(
                            isOnline = isOnline,
                            lastSeenAt = if (!isOnline) Clock.System.now() else participant.lastSeenAt
                        )
                    } else participant
                }
            )
        }
    }
    
    /**
     * Mark a message as read locally
     */
    private fun markMessageAsReadLocally(conversationId: String, messageId: String) {
        _messages.value = _messages.value.mapValues { (convId, msgs) ->
            if (convId == conversationId) {
                msgs.map { msg ->
                    if (msg.id == messageId) msg.copy(isRead = true) else msg
                }
            } else msgs
        }
    }
    
    override fun getConversations(): Flow<Resource<List<Conversation>>> = 
        _conversations.map { conversations ->
            Resource.Success(conversations.sortedByDescending { it.updatedAt })
        }
    
    override fun getConversation(conversationId: String): Flow<Resource<Conversation>> =
        _conversations.map { conversations ->
            val conversation = conversations.find { it.id == conversationId }
            if (conversation != null) {
                Resource.Success(conversation)
            } else {
                Resource.Error("Conversation not found")
            }
        }
    
    override suspend fun getOrCreateConversation(photographerId: String): Resource<Conversation> {
        // Check if conversation already exists
        val existing = _conversations.value.find { 
            it.participantIds.contains(photographerId) && it.participantIds.contains(currentUserId)
        }
        
        if (existing != null) {
            // Join the conversation room for real-time updates
            webSocketService.joinConversation(existing.id)
            return Resource.Success(existing)
        }
        
        // Create new conversation
        val now = Clock.System.now()
        val newConversation = Conversation(
            id = "conv_${now.toEpochMilliseconds()}",
            participantIds = listOf(currentUserId, photographerId),
            participants = listOf(
                ChatParticipant(
                    id = currentUserId,
                    name = currentUserName,
                    userType = ChatUserType.CLIENT,
                    isOnline = true
                ),
                ChatParticipant(
                    id = photographerId,
                    name = getPhotographerName(photographerId),
                    imageUrl = getPhotographerImageUrl(photographerId),
                    userType = ChatUserType.PHOTOGRAPHER,
                    isOnline = true // Assume online for new conversation
                )
            ),
            createdAt = now,
            updatedAt = now
        )
        
        _conversations.value = _conversations.value + newConversation
        _messages.value = _messages.value + (newConversation.id to emptyList())
        
        // Join the conversation room for real-time updates
        webSocketService.joinConversation(newConversation.id)
        
        return Resource.Success(newConversation)
    }
    
    override fun getMessages(conversationId: String): Flow<Resource<List<Message>>> = 
        _messages.map { allMessages ->
            val conversationMessages = allMessages[conversationId] ?: emptyList()
            Resource.Success(conversationMessages.sortedBy { it.timestamp })
        }
    
    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: MessageType,
        attachmentUrl: String?,
        replyToMessageId: String?
    ): Resource<Message> {
        return try {
            // Send via WebSocket (this will also emit locally for immediate feedback)
            val message = webSocketService.sendMessage(
                conversationId = conversationId,
                content = content,
                messageType = messageType,
                attachmentUrl = attachmentUrl,
                replyToMessageId = replyToMessageId
            )
            
            Resource.Success(message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }
    
    override suspend fun markMessagesAsRead(conversationId: String): Resource<Unit> {
        // Mark all messages in conversation as read locally
        val currentMessages = _messages.value[conversationId] ?: return Resource.Success(Unit)
        val unreadMessages = currentMessages.filter { !it.isRead && it.senderId != currentUserId }
        
        // Send read receipts via WebSocket
        unreadMessages.forEach { message ->
            webSocketService.sendMessageRead(conversationId, message.id)
        }
        
        // Update locally
        val updatedMessages = currentMessages.map { it.copy(isRead = true) }
        _messages.value = _messages.value + (conversationId to updatedMessages)
        
        // Reset unread count
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(unreadCount = 0)
            } else conv
        }
        
        return Resource.Success(Unit)
    }
    
    override suspend fun deleteMessage(messageId: String): Resource<Unit> {
        _messages.value = _messages.value.mapValues { (_, msgs) ->
            msgs.filter { it.id != messageId }
        }
        return Resource.Success(Unit)
    }
    
    override suspend fun archiveConversation(conversationId: String): Resource<Unit> {
        // Leave the conversation room
        webSocketService.leaveConversation(conversationId)
        
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(isArchived = true)
            } else conv
        }
        return Resource.Success(Unit)
    }
    
    override fun getTotalUnreadCount(): Flow<Int> = 
        _conversations.map { convs ->
            convs.sumOf { it.unreadCount }
        }
    
    override suspend fun searchMessages(conversationId: String, query: String): Resource<List<Message>> {
        val conversationMessages = _messages.value[conversationId] ?: emptyList()
        val results = conversationMessages.filter { 
            it.content.contains(query, ignoreCase = true) 
        }
        return Resource.Success(results)
    }
    
    /**
     * Send typing indicator
     */
    suspend fun sendTypingIndicator(conversationId: String, isTyping: Boolean) {
        webSocketService.sendTypingIndicator(conversationId, isTyping)
    }
    
    /**
     * Get typing users for a conversation as Flow
     */
    fun getTypingUsers(conversationId: String): Flow<Set<String>> =
        _typingUsers.map { it[conversationId] ?: emptySet() }
    
    /**
     * Helper function to get photographer name (mock)
     */
    private fun getPhotographerName(photographerId: String): String {
        return when (photographerId) {
            "photographer_1", "nearby_1" -> "Alex Rivera"
            "photographer_2", "nearby_2" -> "Sarah Chen"
            "photographer_3", "nearby_3" -> "Marcus Johnson"
            "nearby_4" -> "Emma Williams"
            "nearby_5" -> "David Park"
            "nearby_6" -> "Lisa Thompson"
            "nearby_7" -> "James Wilson"
            "nearby_8" -> "Maria Garcia"
            else -> "Photographer"
        }
    }
    
    /**
     * Helper function to get photographer image URL (mock)
     */
    private fun getPhotographerImageUrl(photographerId: String): String? {
        return when (photographerId) {
            "photographer_1", "nearby_1" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400"
            "photographer_2", "nearby_2" -> "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400"
            "photographer_3", "nearby_3" -> "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400"
            "nearby_4" -> "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400"
            "nearby_5" -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400"
            "nearby_6" -> "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400"
            "nearby_7" -> "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400"
            "nearby_8" -> "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400"
            else -> null
        }
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        webSocketService.cleanup()
    }
}
