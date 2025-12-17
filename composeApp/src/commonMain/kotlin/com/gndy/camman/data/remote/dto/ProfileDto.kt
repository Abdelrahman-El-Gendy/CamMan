package com.gndy.camman.data.remote.dto

import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.model.SocialLinks
import kotlinx.serialization.Serializable

@Serializable
data class PhotographerProfileDto(
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
    val socialLinks: SocialLinksDto,
    val rating: Float,
    val reviewCount: Int
) {
    fun toPhotographerProfile(): PhotographerProfile = PhotographerProfile(
        id = id,
        name = name,
        bio = bio,
        profileImageUrl = profileImageUrl,
        coverImageUrl = coverImageUrl,
        specialties = specialties,
        yearsOfExperience = yearsOfExperience,
        location = location,
        email = email,
        phone = phone,
        website = website,
        socialLinks = socialLinks.toSocialLinks(),
        rating = rating,
        reviewCount = reviewCount
    )
}

@Serializable
data class SocialLinksDto(
    val instagram: String? = null,
    val facebook: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val youtube: String? = null
) {
    fun toSocialLinks(): SocialLinks = SocialLinks(
        instagram = instagram,
        facebook = facebook,
        twitter = twitter,
        linkedin = linkedin,
        youtube = youtube
    )
}

@Serializable
data class ContactMessageRequest(
    val name: String,
    val email: String,
    val subject: String,
    val message: String
)
