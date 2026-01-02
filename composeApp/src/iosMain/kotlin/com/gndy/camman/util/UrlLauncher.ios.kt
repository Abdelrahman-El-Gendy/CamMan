package com.gndy.camman.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS implementation of UrlLauncher
 */
actual class UrlLauncher {
    
    /**
     * Open a URL in the default browser or appropriate app
     */
    actual fun openUrl(url: String): Boolean {
        val nsUrl = NSURL.URLWithString(url) ?: return false
        return UIApplication.sharedApplication.openURL(nsUrl)
    }
    
    /**
     * Open WhatsApp with the given phone number and message
     */
    actual fun openWhatsApp(phoneNumber: String, message: String?): Boolean {
        val cleanedNumber = phoneNumber.replace(Regex("[^0-9+]"), "").removePrefix("+")
        
        // Try WhatsApp URL scheme first
        val whatsappUrl = if (message != null) {
            val encodedMessage = message.encodeForUrl()
            "whatsapp://send?phone=$cleanedNumber&text=$encodedMessage"
        } else {
            "whatsapp://send?phone=$cleanedNumber"
        }
        
        val nsUrl = NSURL.URLWithString(whatsappUrl)
        if (nsUrl != null && UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            return UIApplication.sharedApplication.openURL(nsUrl)
        }
        
        // Fall back to web URL
        return openUrl(WhatsAppUtils.generateWhatsAppUri(phoneNumber, message))
    }
    
    /**
     * Check if WhatsApp is installed on the device
     */
    actual fun isWhatsAppInstalled(): Boolean {
        val whatsappUrl = NSURL.URLWithString("whatsapp://")
        return whatsappUrl != null && UIApplication.sharedApplication.canOpenURL(whatsappUrl)
    }
    
    private fun String.encodeForUrl(): String {
        return buildString {
            this@encodeForUrl.forEach { char ->
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
