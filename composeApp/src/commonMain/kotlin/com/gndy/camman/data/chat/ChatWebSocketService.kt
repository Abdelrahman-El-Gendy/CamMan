package com.gndy.camman.data.chat

import com.gndy.camman.domain.model.Message
import com.gndy.camman.domain.model.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Connection state for WebSocket
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ERROR
}

/**
 * WebSocket message types for the chat protocol
 */
@Serializable
sealed class WebSocketMessage {
    @Serializable
    data class ChatMessage(
        val id: String,
        val conversationId: String,
        val senderId: String,
        val senderName: String,
        val senderImageUrl: String? = null,
        val content: String,
        val timestamp: Long,
        val messageType: String = "TEXT",
        val attachmentUrl: String? = null,
        val replyToMessageId: String? = null
    ) : WebSocketMessage()
    
    @Serializable
    data class TypingIndicator(
        val conversationId: String,
        val userId: String,
        val userName: String,
        val isTyping: Boolean
    ) : WebSocketMessage()
    
    @Serializable
    data class MessageRead(
        val conversationId: String,
        val messageId: String,
        val readBy: String,
        val readAt: Long
    ) : WebSocketMessage()
    
    @Serializable
    data class UserPresence(
        val userId: String,
        val isOnline: Boolean,
        val lastSeenAt: Long?
    ) : WebSocketMessage()
    
    @Serializable
    data class JoinConversation(
        val conversationId: String,
        val userId: String
    ) : WebSocketMessage()
    
    @Serializable
    data class LeaveConversation(
        val conversationId: String,
        val userId: String
    ) : WebSocketMessage()
}

/**
 * WebSocket-based real-time chat service
 */
class ChatWebSocketService {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    private val httpClient = HttpClient {
        install(WebSockets) {
            pingIntervalMillis = 30_000 // 30 seconds
        }
    }
    
    private var webSocketSession: WebSocketSession? = null
    
    // Connection state
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    // Incoming messages
    private val _incomingMessages = MutableSharedFlow<Message>(replay = 0)
    val incomingMessages: SharedFlow<Message> = _incomingMessages.asSharedFlow()
    
    // Typing indicators
    private val _typingIndicators = MutableSharedFlow<WebSocketMessage.TypingIndicator>(replay = 0)
    val typingIndicators: SharedFlow<WebSocketMessage.TypingIndicator> = _typingIndicators.asSharedFlow()
    
    // User presence updates
    private val _userPresence = MutableSharedFlow<WebSocketMessage.UserPresence>(replay = 0)
    val userPresence: SharedFlow<WebSocketMessage.UserPresence> = _userPresence.asSharedFlow()
    
    // Message read receipts
    private val _messageReadReceipts = MutableSharedFlow<WebSocketMessage.MessageRead>(replay = 0)
    val messageReadReceipts: SharedFlow<WebSocketMessage.MessageRead> = _messageReadReceipts.asSharedFlow()
    
    // Current user ID (should be set after authentication)
    private var currentUserId: String = "current_user"
    private var currentUserName: String = "You"
    
    /**
     * Connect to WebSocket server
     */
    suspend fun connect(
        serverUrl: String = "wss://api.camman.com/chat/ws",
        userId: String,
        userName: String,
        authToken: String? = null
    ) {
        if (_connectionState.value == ConnectionState.CONNECTED) return
        
        currentUserId = userId
        currentUserName = userName
        _connectionState.value = ConnectionState.CONNECTING
        
        try {
            // For demo purposes, we'll simulate the connection
            // In production, replace with actual WebSocket connection:
            /*
            httpClient.webSocket(serverUrl) {
                webSocketSession = this
                _connectionState.value = ConnectionState.CONNECTED
                
                // Listen for incoming messages
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            handleIncomingMessage(frame.readText())
                        }
                        else -> {}
                    }
                }
            }
            */
            
            // Simulated connection for demo
            _connectionState.value = ConnectionState.CONNECTED
            simulateRealTimeMessages()
            
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            println("WebSocket connection error: ${e.message}")
        }
    }
    
    /**
     * Disconnect from WebSocket server
     */
    suspend fun disconnect() {
        try {
            webSocketSession?.close()
            webSocketSession = null
            _connectionState.value = ConnectionState.DISCONNECTED
            scope.coroutineContext.cancelChildren()
        } catch (e: Exception) {
            println("WebSocket disconnect error: ${e.message}")
        }
    }
    
    /**
     * Send a chat message
     */
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        attachmentUrl: String? = null,
        replyToMessageId: String? = null
    ): Message {
        val now = Clock.System.now()
        val messageId = "msg_${now.toEpochMilliseconds()}"
        
        val wsMessage = WebSocketMessage.ChatMessage(
            id = messageId,
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            content = content,
            timestamp = now.toEpochMilliseconds(),
            messageType = messageType.name,
            attachmentUrl = attachmentUrl,
            replyToMessageId = replyToMessageId
        )
        
        // Send via WebSocket
        sendWebSocketMessage(wsMessage)
        
        // Return the message object
        return Message(
            id = messageId,
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            content = content,
            timestamp = now,
            isRead = false,
            messageType = messageType,
            attachmentUrl = attachmentUrl,
            replyToMessageId = replyToMessageId
        )
    }
    
    /**
     * Send typing indicator
     */
    suspend fun sendTypingIndicator(conversationId: String, isTyping: Boolean) {
        val indicator = WebSocketMessage.TypingIndicator(
            conversationId = conversationId,
            userId = currentUserId,
            userName = currentUserName,
            isTyping = isTyping
        )
        sendWebSocketMessage(indicator)
    }
    
    /**
     * Send message read receipt
     */
    suspend fun sendMessageRead(conversationId: String, messageId: String) {
        val readReceipt = WebSocketMessage.MessageRead(
            conversationId = conversationId,
            messageId = messageId,
            readBy = currentUserId,
            readAt = Clock.System.now().toEpochMilliseconds()
        )
        sendWebSocketMessage(readReceipt)
    }
    
    /**
     * Join a conversation room
     */
    suspend fun joinConversation(conversationId: String) {
        val join = WebSocketMessage.JoinConversation(
            conversationId = conversationId,
            userId = currentUserId
        )
        sendWebSocketMessage(join)
    }
    
    /**
     * Leave a conversation room
     */
    suspend fun leaveConversation(conversationId: String) {
        val leave = WebSocketMessage.LeaveConversation(
            conversationId = conversationId,
            userId = currentUserId
        )
        sendWebSocketMessage(leave)
    }
    
    private suspend fun sendWebSocketMessage(message: WebSocketMessage) {
        try {
            val jsonString = when (message) {
                is WebSocketMessage.ChatMessage -> json.encodeToString(message)
                is WebSocketMessage.TypingIndicator -> json.encodeToString(message)
                is WebSocketMessage.MessageRead -> json.encodeToString(message)
                is WebSocketMessage.UserPresence -> json.encodeToString(message)
                is WebSocketMessage.JoinConversation -> json.encodeToString(message)
                is WebSocketMessage.LeaveConversation -> json.encodeToString(message)
            }
            
            webSocketSession?.send(Frame.Text(jsonString))
            
            // For demo: also emit locally for immediate feedback
            if (message is WebSocketMessage.ChatMessage) {
                val localMessage = Message(
                    id = message.id,
                    conversationId = message.conversationId,
                    senderId = message.senderId,
                    senderName = message.senderName,
                    senderImageUrl = message.senderImageUrl,
                    content = message.content,
                    timestamp = Instant.fromEpochMilliseconds(message.timestamp),
                    messageType = MessageType.valueOf(message.messageType),
                    attachmentUrl = message.attachmentUrl,
                    replyToMessageId = message.replyToMessageId
                )
                _incomingMessages.emit(localMessage)
            }
        } catch (e: Exception) {
            println("Error sending WebSocket message: ${e.message}")
        }
    }
    
    private suspend fun handleIncomingMessage(jsonString: String) {
        try {
            // Try to parse as different message types
            // In production, you'd have a type discriminator in the JSON
            
            val chatMessage = runCatching {
                json.decodeFromString<WebSocketMessage.ChatMessage>(jsonString)
            }.getOrNull()
            
            if (chatMessage != null) {
                val message = Message(
                    id = chatMessage.id,
                    conversationId = chatMessage.conversationId,
                    senderId = chatMessage.senderId,
                    senderName = chatMessage.senderName,
                    senderImageUrl = chatMessage.senderImageUrl,
                    content = chatMessage.content,
                    timestamp = Instant.fromEpochMilliseconds(chatMessage.timestamp),
                    messageType = MessageType.valueOf(chatMessage.messageType),
                    attachmentUrl = chatMessage.attachmentUrl,
                    replyToMessageId = chatMessage.replyToMessageId
                )
                _incomingMessages.emit(message)
                return
            }
            
            val typingIndicator = runCatching {
                json.decodeFromString<WebSocketMessage.TypingIndicator>(jsonString)
            }.getOrNull()
            
            if (typingIndicator != null) {
                _typingIndicators.emit(typingIndicator)
                return
            }
            
            val readReceipt = runCatching {
                json.decodeFromString<WebSocketMessage.MessageRead>(jsonString)
            }.getOrNull()
            
            if (readReceipt != null) {
                _messageReadReceipts.emit(readReceipt)
                return
            }
            
            val presence = runCatching {
                json.decodeFromString<WebSocketMessage.UserPresence>(jsonString)
            }.getOrNull()
            
            if (presence != null) {
                _userPresence.emit(presence)
                return
            }
            
        } catch (e: Exception) {
            println("Error parsing incoming message: ${e.message}")
        }
    }
    
    /**
     * Simulate real-time messages for demo purposes
     * Remove this in production and use actual WebSocket messages
     */
    private fun simulateRealTimeMessages() {
        scope.launch {
            // Simulate photographer responses after user sends a message
            incomingMessages.collect { sentMessage ->
                if (sentMessage.senderId == currentUserId) {
                    // Simulate typing indicator
                    kotlinx.coroutines.delay(500)
                    _typingIndicators.emit(
                        WebSocketMessage.TypingIndicator(
                            conversationId = sentMessage.conversationId,
                            userId = "photographer_1",
                            userName = "Alex Rivera",
                            isTyping = true
                        )
                    )
                    
                    // Simulate response after a delay
                    kotlinx.coroutines.delay(2000)
                    
                    // Stop typing
                    _typingIndicators.emit(
                        WebSocketMessage.TypingIndicator(
                            conversationId = sentMessage.conversationId,
                            userId = "photographer_1",
                            userName = "Alex Rivera",
                            isTyping = false
                        )
                    )
                    
                    // Send simulated response
                    val responses = listOf(
                        "Thanks for your message! I'd be happy to help.",
                        "That sounds great! Let me check my availability.",
                        "I appreciate your interest! What date were you thinking?",
                        "Perfect! I have some openings next month.",
                        "Sure thing! I'll send you more details shortly."
                    )
                    
                    val responseMessage = Message(
                        id = "msg_response_${Clock.System.now().toEpochMilliseconds()}",
                        conversationId = sentMessage.conversationId,
                        senderId = "photographer_1",
                        senderName = "Alex Rivera",
                        senderImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                        content = responses.random(),
                        timestamp = Clock.System.now(),
                        isRead = false,
                        messageType = MessageType.TEXT
                    )
                    
                    _incomingMessages.emit(responseMessage)
                }
            }
        }
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.coroutineContext.cancelChildren()
        httpClient.close()
    }
}
