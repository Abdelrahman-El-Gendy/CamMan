package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.PhotographerRegistration

@Entity(tableName = "photographer_registration")
data class PhotographerRegistrationEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val bio: String,
    val location: String,
    val specialties: String, // Comma-separated list
    val experienceLevel: String,
    val yearsOfExperience: Int,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val portfolioUrls: String, // Comma-separated list
    val startingPrice: Double,
    val currency: String,
    val website: String?,
    val instagram: String?,
    val facebook: String?,
    val isAvailable: Boolean,
    val isProfileComplete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toPhotographerRegistration(): PhotographerRegistration {
        return PhotographerRegistration(
            id = id,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            bio = bio,
            location = location,
            specialties = specialties.split(",").filter { it.isNotBlank() },
            experienceLevel = experienceLevel,
            yearsOfExperience = yearsOfExperience,
            profileImageUrl = profileImageUrl,
            coverImageUrl = coverImageUrl,
            portfolioUrls = portfolioUrls.split(",").filter { it.isNotBlank() },
            startingPrice = startingPrice,
            currency = currency,
            website = website,
            instagram = instagram,
            facebook = facebook,
            isAvailable = isAvailable,
            isProfileComplete = isProfileComplete,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromPhotographerRegistration(registration: PhotographerRegistration): PhotographerRegistrationEntity {
            return PhotographerRegistrationEntity(
                id = registration.id,
                email = registration.email,
                fullName = registration.fullName,
                phoneNumber = registration.phoneNumber,
                bio = registration.bio,
                location = registration.location,
                specialties = registration.specialties.joinToString(","),
                experienceLevel = registration.experienceLevel,
                yearsOfExperience = registration.yearsOfExperience,
                profileImageUrl = registration.profileImageUrl,
                coverImageUrl = registration.coverImageUrl,
                portfolioUrls = registration.portfolioUrls.joinToString(","),
                startingPrice = registration.startingPrice,
                currency = registration.currency,
                website = registration.website,
                instagram = registration.instagram,
                facebook = registration.facebook,
                isAvailable = registration.isAvailable,
                isProfileComplete = registration.isProfileComplete,
                createdAt = registration.createdAt,
                updatedAt = registration.updatedAt
            )
        }
    }
}
