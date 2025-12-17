package com.gndy.camman.domain.model

data class PhotographerProfile(
    val id: String,
    val name: String,
    val bio: String,
    val profileImageUrl: String,
    val coverImageUrl: String,
    val specialties: List<String>,
    val yearsOfExperience: Int,
    val location: String,
    val email: String,
    val phone: String,
    val website: String?,
    val socialLinks: SocialLinks,
    val rating: Float,
    val reviewCount: Int
)

data class SocialLinks(
    val instagram: String?,
    val facebook: String?,
    val twitter: String?,
    val linkedin: String?,
    val youtube: String?
)
