package com.gndy.camman.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.model.SocialLinks

@Entity(tableName = "photographer_profile")
data class PhotographerProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val bio: String,
    val profileImageUrl: String,
    val coverImageUrl: String,
    val specialties: String,
    val yearsOfExperience: Int,
    val location: String,
    val email: String,
    val phone: String,
    val website: String?,
    @Embedded(prefix = "social_")
    val socialLinks: SocialLinksEmbedded,
    val rating: Float,
    val reviewCount: Int
) {
    fun toPhotographerProfile(): PhotographerProfile = PhotographerProfile(
        id = id,
        name = name,
        bio = bio,
        profileImageUrl = profileImageUrl,
        coverImageUrl = coverImageUrl,
        specialties = specialties.split(",").filter { it.isNotBlank() },
        yearsOfExperience = yearsOfExperience,
        location = location,
        email = email,
        phone = phone,
        website = website,
        socialLinks = socialLinks.toSocialLinks(),
        rating = rating,
        reviewCount = reviewCount
    )

    companion object {
        fun fromPhotographerProfile(profile: PhotographerProfile): PhotographerProfileEntity =
            PhotographerProfileEntity(
                id = profile.id,
                name = profile.name,
                bio = profile.bio,
                profileImageUrl = profile.profileImageUrl,
                coverImageUrl = profile.coverImageUrl,
                specialties = profile.specialties.joinToString(","),
                yearsOfExperience = profile.yearsOfExperience,
                location = profile.location,
                email = profile.email,
                phone = profile.phone,
                website = profile.website,
                socialLinks = SocialLinksEmbedded.fromSocialLinks(profile.socialLinks),
                rating = profile.rating,
                reviewCount = profile.reviewCount
            )
    }
}

data class SocialLinksEmbedded(
    val instagram: String?,
    val facebook: String?,
    val twitter: String?,
    val linkedin: String?,
    val youtube: String?
) {
    fun toSocialLinks(): SocialLinks = SocialLinks(
        instagram = instagram,
        facebook = facebook,
        twitter = twitter,
        linkedin = linkedin,
        youtube = youtube
    )

    companion object {
        fun fromSocialLinks(links: SocialLinks): SocialLinksEmbedded = SocialLinksEmbedded(
            instagram = links.instagram,
            facebook = links.facebook,
            twitter = links.twitter,
            linkedin = links.linkedin,
            youtube = links.youtube
        )
    }
}
