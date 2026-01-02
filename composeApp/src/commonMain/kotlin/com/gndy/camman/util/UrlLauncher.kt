package com.gndy.camman.util

/**
 * Platform-specific URL launcher interface
 */
expect class UrlLauncher {
    /**
     * Open a URL in the default browser or appropriate app
     * @return true if successful, false otherwise
     */
    fun openUrl(url: String): Boolean
    
    /**
     * Open WhatsApp with the given phone number and message
     * @return true if WhatsApp was opened successfully
     */
    fun openWhatsApp(phoneNumber: String, message: String? = null): Boolean
    
    /**
     * Check if WhatsApp is installed on the device
     */
    fun isWhatsAppInstalled(): Boolean
}
