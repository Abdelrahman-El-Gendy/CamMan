package com.gndy.camman.domain.model

/**
 * Photographer profile for display in the browse list
 */
data class Photographer(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val bio: String?,
    val specialties: List<String>,
    val location: String?,
    val rating: Float,
    val reviewCount: Int,
    val startingPrice: Double?,
    val currency: String = "USD",
    val isAvailable: Boolean = true,
    val portfolioPreviewUrls: List<String> = emptyList(),
    val phone: String? = null,
    val whatsappNumber: String? = null, // Can be different from phone
    val email: String? = null
)
