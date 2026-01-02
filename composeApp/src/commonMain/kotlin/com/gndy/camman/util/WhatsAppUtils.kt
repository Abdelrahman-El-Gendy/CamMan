package com.gndy.camman.util

/**
 * WhatsApp Integration Utility
 * Provides methods to generate WhatsApp URIs for cross-platform communication
 */
object WhatsAppUtils {
    
    // WhatsApp brand color
    const val WHATSAPP_GREEN = 0xFF25D366
    
    /**
     * Generate WhatsApp URI for sending a message
     * 
     * @param phoneNumber Phone number with country code (e.g., "+1234567890")
     * @param message Pre-filled message (optional)
     * @return URI string that can be used to open WhatsApp
     */
    fun generateWhatsAppUri(phoneNumber: String, message: String? = null): String {
        // Remove any non-numeric characters except +
        val cleanedNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        // Remove the + for the URI
        val numberForUri = cleanedNumber.removePrefix("+")
        
        return if (message != null) {
            val encodedMessage = message.encodeUrl()
            "https://wa.me/$numberForUri?text=$encodedMessage"
        } else {
            "https://wa.me/$numberForUri"
        }
    }
    
    /**
     * Generate WhatsApp deep link for mobile apps
     * 
     * @param phoneNumber Phone number with country code
     * @param message Pre-filled message (optional)
     * @return Deep link URI for WhatsApp app
     */
    fun generateWhatsAppDeepLink(phoneNumber: String, message: String? = null): String {
        val cleanedNumber = phoneNumber.replace(Regex("[^0-9+]"), "").removePrefix("+")
        
        return if (message != null) {
            val encodedMessage = message.encodeUrl()
            "whatsapp://send?phone=$cleanedNumber&text=$encodedMessage"
        } else {
            "whatsapp://send?phone=$cleanedNumber"
        }
    }
    
    /**
     * Generate a default message for contacting a photographer
     */
    fun generatePhotographerMessage(photographerName: String, context: MessageContext = MessageContext.GENERAL): String {
        return when (context) {
            MessageContext.GENERAL -> 
                "Hi $photographerName! I found you on CameraMan app and I'm interested in your photography services."
            MessageContext.BOOKING_INQUIRY ->
                "Hi $photographerName! I'd like to inquire about booking a photography session. I found you on CameraMan."
            MessageContext.AVAILABILITY_CHECK ->
                "Hi $photographerName! I'm checking if you're available for a photo session. I found you on CameraMan."
            MessageContext.PRICE_INQUIRY ->
                "Hi $photographerName! I'd like to know more about your pricing and packages. I found you on CameraMan."
        }
    }
    
    /**
     * Context for pre-filled messages
     */
    enum class MessageContext {
        GENERAL,
        BOOKING_INQUIRY,
        AVAILABILITY_CHECK,
        PRICE_INQUIRY
    }
    
    /**
     * URL encode a string for use in URIs
     */
    private fun String.encodeUrl(): String {
        return buildString {
            this@encodeUrl.forEach { char ->
                when (char) {
                    ' ' -> append("%20")
                    '!' -> append("%21")
                    '"' -> append("%22")
                    '#' -> append("%23")
                    '$' -> append("%24")
                    '%' -> append("%25")
                    '&' -> append("%26")
                    '\'' -> append("%27")
                    '(' -> append("%28")
                    ')' -> append("%29")
                    '*' -> append("%2A")
                    '+' -> append("%2B")
                    ',' -> append("%2C")
                    '/' -> append("%2F")
                    ':' -> append("%3A")
                    ';' -> append("%3B")
                    '=' -> append("%3D")
                    '?' -> append("%3F")
                    '@' -> append("%40")
                    '[' -> append("%5B")
                    ']' -> append("%5D")
                    else -> append(char)
                }
            }
        }
    }
}

/**
 * Data class to hold photographer contact information
 */
data class PhotographerContact(
    val id: String,
    val name: String,
    val phoneNumber: String?,
    val whatsappNumber: String? = phoneNumber, // Can be different from primary phone
    val email: String?
)
