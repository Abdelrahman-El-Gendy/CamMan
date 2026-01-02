package com.gndy.camman.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Supabase configuration for CameraMan app
 * 
 * Production-ready authentication setup that supports multiple users
 */
object SupabaseConfig {
    // Supabase Project URL and Anonymous Key
    // These are safe to expose in the client as they only allow authenticated operations
    private const val SUPABASE_URL = "https://oetrzphsxvxwlkgyqphf.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9ldHJ6cGhzeHZ4d2xrZ3lxcGhmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg1NDg4NzksImV4cCI6MjA3NDEyNDg3OX0.oUvu4-QCK0AgBWDVYRb1pEfF8LIyTJVZcQtDVyMTRec"

    /**
     * Creates and configures the Supabase client with Auth, Postgrest, and Realtime plugins
     */
    fun createClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            // Auth plugin for authentication
            install(Auth) {
                // Auto-refresh tokens to keep session alive
                autoSaveToStorage = true
                autoLoadFromStorage = true
            }
            
            // Postgrest plugin for database operations
            install(Postgrest)
            
            // Realtime plugin for live updates
            install(Realtime)
        }
    }
}

/**
 * Extension property to get the Auth instance from SupabaseClient
 */
val SupabaseClient.supabaseAuth: Auth
    get() = auth
