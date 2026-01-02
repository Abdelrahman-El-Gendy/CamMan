package com.gndy.camman.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Message delivery status
 */
enum class MessageDeliveryStatus {
    SENDING,    // Message is being sent
    SENT,       // Message sent to server
    DELIVERED,  // Message delivered to recipient's device
    READ,       // Message has been read by recipient
    FAILED      // Message failed to send
}

/**
 * Represents a chat message between a client and photographer
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderImageUrl: String? = null,
    val content: String,
    val timestamp: Instant = Clock.System.now(),
    val isRead: Boolean = false,
    val deliveryStatus: MessageDeliveryStatus = MessageDeliveryStatus.SENT,
    val messageType: MessageType = MessageType.TEXT,
    val attachmentUrl: String? = null,
    val attachmentThumbnailUrl: String? = null,
    val replyToMessageId: String? = null,
    val bookingId: String? = null  // Link to associated booking if any
)

/**
 * Types of messages that can be sent
 */
enum class MessageType {
    TEXT,
    IMAGE,
    BOOKING_REQUEST,
    BOOKING_CONFIRMATION,
    QUOTE_REQUEST,
    QUOTE_RESPONSE,
    SYSTEM
}

/**
 * Represents a conversation between a client and photographer
 */
data class Conversation(
    val id: String,
    val participantIds: List<String>,
    val participants: List<ChatParticipant>,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val isArchived: Boolean = false,
    val bookingId: String? = null // Link to associated booking if any
)

/**
 * Represents a participant in a chat conversation
 */
data class ChatParticipant(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val userType: ChatUserType,
    val isOnline: Boolean = false,
    val lastSeenAt: Instant? = null
)

/**
 * Type of user in the chat
 */
enum class ChatUserType {
    CLIENT,
    PHOTOGRAPHER
}

/**
 * Quick reply suggestions for common responses
 */
data class QuickReply(
    val id: String,
    val text: String,
    val category: QuickReplyCategory
)

enum class QuickReplyCategory {
    GREETING,
    AVAILABILITY,
    PRICING,
    BOOKING,
    GENERAL
}

/**
 * Predefined quick replies for photographers
 */
object PhotographerQuickReplies {
    val greetings = listOf(
        QuickReply("g1", "Hi! Thanks for reaching out. How can I help you?", QuickReplyCategory.GREETING),
        QuickReply("g2", "Hello! I'd be happy to discuss your photography needs.", QuickReplyCategory.GREETING)
    )
    
    val availability = listOf(
        QuickReply("a1", "I'm available on that date! Would you like to proceed with booking?", QuickReplyCategory.AVAILABILITY),
        QuickReply("a2", "Unfortunately, I'm booked on that date. Would another date work for you?", QuickReplyCategory.AVAILABILITY),
        QuickReply("a3", "Let me check my calendar and get back to you shortly.", QuickReplyCategory.AVAILABILITY)
    )
    
    val pricing = listOf(
        QuickReply("p1", "My packages start from the prices listed on my profile. Would you like more details?", QuickReplyCategory.PRICING),
        QuickReply("p2", "I can create a custom quote based on your specific needs.", QuickReplyCategory.PRICING)
    )
    
    val booking = listOf(
        QuickReply("b1", "Great! I'll send you a booking request now.", QuickReplyCategory.BOOKING),
        QuickReply("b2", "To secure your booking, please complete the payment through the app.", QuickReplyCategory.BOOKING)
    )
    
    val all = greetings + availability + pricing + booking
}

/**
 * Predefined quick replies for clients
 */
object ClientQuickReplies {
    val greetings = listOf(
        QuickReply("cg1", "Hi! I'm interested in your photography services.", QuickReplyCategory.GREETING),
        QuickReply("cg2", "Hello! I love your portfolio work.", QuickReplyCategory.GREETING)
    )
    
    val availability = listOf(
        QuickReply("ca1", "Are you available on [date]?", QuickReplyCategory.AVAILABILITY),
        QuickReply("ca2", "What dates do you have available this month?", QuickReplyCategory.AVAILABILITY)
    )
    
    val pricing = listOf(
        QuickReply("cp1", "Can you tell me more about your pricing?", QuickReplyCategory.PRICING),
        QuickReply("cp2", "Do you offer any package deals?", QuickReplyCategory.PRICING)
    )
    
    val booking = listOf(
        QuickReply("cb1", "I'd like to book a session.", QuickReplyCategory.BOOKING),
        QuickReply("cb2", "How do I proceed with the booking?", QuickReplyCategory.BOOKING)
    )
    
    val all = greetings + availability + pricing + booking
}
