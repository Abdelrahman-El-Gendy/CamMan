package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.Conversation
import com.gndy.camman.domain.model.Message
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for chat functionality
 */
interface ChatRepository {
    
    /**
     * Get all conversations for the current user
     */
    fun getConversations(): Flow<Resource<List<Conversation>>>
    
    /**
     * Get a specific conversation by ID
     */
    fun getConversation(conversationId: String): Flow<Resource<Conversation>>
    
    /**
     * Get or create a conversation with a specific photographer
     */
    suspend fun getOrCreateConversation(photographerId: String): Resource<Conversation>
    
    /**
     * Get messages for a conversation
     */
    fun getMessages(conversationId: String): Flow<Resource<List<Message>>>
    
    /**
     * Send a message in a conversation
     */
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: com.gndy.camman.domain.model.MessageType = com.gndy.camman.domain.model.MessageType.TEXT,
        attachmentUrl: String? = null,
        replyToMessageId: String? = null
    ): Resource<Message>
    
    /**
     * Mark messages as read
     */
    suspend fun markMessagesAsRead(conversationId: String): Resource<Unit>
    
    /**
     * Delete a message
     */
    suspend fun deleteMessage(messageId: String): Resource<Unit>
    
    /**
     * Archive a conversation
     */
    suspend fun archiveConversation(conversationId: String): Resource<Unit>
    
    /**
     * Get unread message count across all conversations
     */
    fun getTotalUnreadCount(): Flow<Int>
    
    /**
     * Search messages in a conversation
     */
    suspend fun searchMessages(conversationId: String, query: String): Resource<List<Message>>
}
