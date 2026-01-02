package com.gndy.camman.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * Android implementation of UrlLauncher
 */
actual class UrlLauncher(private val context: Context) {
    
    /**
     * Open a URL in the default browser or appropriate app
     */
    actual fun openUrl(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Open WhatsApp with the given phone number and message
     */
    actual fun openWhatsApp(phoneNumber: String, message: String?): Boolean {
        val cleanedNumber = phoneNumber.replace(Regex("[^0-9+]"), "").removePrefix("+")
        
        return try {
            // Try WhatsApp deep link first
            val whatsappUri = if (message != null) {
                Uri.parse("whatsapp://send?phone=$cleanedNumber&text=${Uri.encode(message)}")
            } else {
                Uri.parse("whatsapp://send?phone=$cleanedNumber")
            }
            
            val intent = Intent(Intent.ACTION_VIEW, whatsappUri).apply {
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                // Fall back to web URL
                openUrl(WhatsAppUtils.generateWhatsAppUri(phoneNumber, message))
            }
        } catch (e: Exception) {
            // Fall back to web URL on any error
            openUrl(WhatsAppUtils.generateWhatsAppUri(phoneNumber, message))
        }
    }
    
    /**
     * Check if WhatsApp is installed on the device
     */
    actual fun isWhatsAppInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            // Also check for WhatsApp Business
            try {
                context.packageManager.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES)
                true
            } catch (e2: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
}
