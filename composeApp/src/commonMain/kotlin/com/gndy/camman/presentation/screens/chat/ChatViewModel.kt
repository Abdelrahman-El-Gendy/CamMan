package com.gndy.camman.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.data.chat.ConnectionState
import com.gndy.camman.data.repository.ChatRepositoryImpl
import com.gndy.camman.domain.model.Conversation
import com.gndy.camman.domain.model.Message
import com.gndy.camman.domain.model.MessageType
import com.gndy.camman.domain.model.QuickReply
import com.gndy.camman.domain.model.ClientQuickReplies
import com.gndy.camman.domain.repository.ChatRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Chat screen
 */
data class ChatUiState(
    val isLoading: Boolean = true,
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val quickReplies: List<QuickReply> = ClientQuickReplies.all,
    val showQuickReplies: Boolean = false,
    val replyToMessage: Message? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val typingUsers: Set<String> = emptySet(),
    val isOtherUserTyping: Boolean = false
)

/**
 * One-time UI events
 */
sealed class ChatUiEvent {
    data class ShowError(val message: String) : ChatUiEvent()
    data object MessageSent : ChatUiEvent()
    data object ScrollToBottom : ChatUiEvent()
}

/**
 * ViewModel for Chat screen
 */
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val _uiEvents = MutableSharedFlow<ChatUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()
    
    private var currentConversationId: String? = null
    private var typingJob: Job? = null
    
    // Cast to implementation to access real-time features
    private val chatRepoImpl: ChatRepositoryImpl? 
        get() = chatRepository as? ChatRepositoryImpl
    
    /**
     * Get the current user's ID for identifying own messages
     */
    val currentUserId: String
        get() = chatRepoImpl?.currentUserId ?: "client_user"
    
    init {
        // Observe connection state
        viewModelScope.launch {
            chatRepoImpl?.connectionState?.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }
    }
    
    /**
     * Load conversation and messages
     */
    fun loadConversation(conversationId: String) {
        currentConversationId = conversationId
        
        viewModelScope.launch {
            // Load conversation details
            chatRepository.getConversation(conversationId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                conversation = resource.data,
                                error = null
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            ) 
                        }
                    }
                }
            }
        }
        
        viewModelScope.launch {
            // Load messages
            chatRepository.getMessages(conversationId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(messages = resource.data ?: emptyList()) 
                        }
                        _uiEvents.emit(ChatUiEvent.ScrollToBottom)
                    }
                    else -> {}
                }
            }
        }
        
        // Mark messages as read
        viewModelScope.launch {
            chatRepository.markMessagesAsRead(conversationId)
        }
        
        // Observe typing indicators for this conversation
        viewModelScope.launch {
            chatRepoImpl?.getTypingUsers(conversationId)?.collect { typingUserIds ->
                // Filter out current user
                val otherTypingUsers = typingUserIds.filter { it != "current_user" }
                _uiState.update { 
                    it.copy(
                        typingUsers = otherTypingUsers.toSet(),
                        isOtherUserTyping = otherTypingUsers.isNotEmpty()
                    ) 
                }
            }
        }
    }
    
    /**
     * Load or create conversation with a photographer
     */
    fun loadOrCreateConversation(photographerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = chatRepository.getOrCreateConversation(photographerId)) {
                is Resource.Success -> {
                    result.data?.let { conversation ->
                        loadConversation(conversation.id)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        ) 
                    }
                    _uiEvents.emit(ChatUiEvent.ShowError(result.message ?: "Failed to load chat"))
                }
                else -> {}
            }
        }
    }
    
    /**
     * Update message text and send typing indicator
     */
    fun onMessageTextChanged(text: String) {
        _uiState.update { it.copy(messageText = text) }
        
        // Send typing indicator with debounce
        val conversationId = currentConversationId ?: return
        
        // Cancel previous typing job
        typingJob?.cancel()
        
        // Send typing started
        if (text.isNotEmpty()) {
            viewModelScope.launch {
                chatRepoImpl?.sendTypingIndicator(conversationId, true)
            }
            
            // Auto-stop typing after 3 seconds of no input
            typingJob = viewModelScope.launch {
                delay(3000)
                chatRepoImpl?.sendTypingIndicator(conversationId, false)
            }
        } else {
            // Stopped typing
            viewModelScope.launch {
                chatRepoImpl?.sendTypingIndicator(conversationId, false)
            }
        }
    }
    
    /**
     * Send a message
     */
    fun sendMessage() {
        val text = _uiState.value.messageText.trim()
        if (text.isEmpty()) return
        
        val conversationId = currentConversationId ?: return
        val replyTo = _uiState.value.replyToMessage?.id
        
        // Stop typing indicator
        typingJob?.cancel()
        viewModelScope.launch {
            chatRepoImpl?.sendTypingIndicator(conversationId, false)
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, messageText = "", replyToMessage = null) }
            
            when (val result = chatRepository.sendMessage(
                conversationId = conversationId,
                content = text,
                messageType = MessageType.TEXT,
                replyToMessageId = replyTo
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSending = false) }
                    _uiEvents.emit(ChatUiEvent.MessageSent)
                    _uiEvents.emit(ChatUiEvent.ScrollToBottom)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSending = false,
                            messageText = text // Restore message on error
                        ) 
                    }
                    _uiEvents.emit(ChatUiEvent.ShowError(result.message ?: "Failed to send message"))
                }
                else -> {}
            }
        }
    }
    
    /**
     * Use a quick reply
     */
    fun useQuickReply(quickReply: QuickReply) {
        _uiState.update { 
            it.copy(
                messageText = quickReply.text,
                showQuickReplies = false
            ) 
        }
    }
    
    /**
     * Toggle quick replies visibility
     */
    fun toggleQuickReplies() {
        _uiState.update { it.copy(showQuickReplies = !it.showQuickReplies) }
    }
    
    /**
     * Set reply to message
     */
    fun setReplyToMessage(message: Message?) {
        _uiState.update { it.copy(replyToMessage = message) }
    }
    
    /**
     * Cancel reply
     */
    fun cancelReply() {
        _uiState.update { it.copy(replyToMessage = null) }
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for Conversations List screen
 */
data class ConversationsListUiState(
    val isLoading: Boolean = true,
    val conversations: List<Conversation> = emptyList(),
    val error: String? = null,
    val totalUnreadCount: Int = 0
)

/**
 * ViewModel for Conversations List screen
 */
class ConversationsListViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConversationsListUiState())
    val uiState: StateFlow<ConversationsListUiState> = _uiState.asStateFlow()
    
    init {
        loadConversations()
        observeUnreadCount()
    }
    
    private fun loadConversations() {
        viewModelScope.launch {
            chatRepository.getConversations().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                conversations = resource.data?.filter { conv -> !conv.isArchived } ?: emptyList(),
                                error = null
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            ) 
                        }
                    }
                }
            }
        }
    }
    
    private fun observeUnreadCount() {
        viewModelScope.launch {
            chatRepository.getTotalUnreadCount().collect { count ->
                _uiState.update { it.copy(totalUnreadCount = count) }
            }
        }
    }
    
    fun refresh() {
        loadConversations()
    }
    
    fun archiveConversation(conversationId: String) {
        viewModelScope.launch {
            chatRepository.archiveConversation(conversationId)
            loadConversations()
        }
    }
}
