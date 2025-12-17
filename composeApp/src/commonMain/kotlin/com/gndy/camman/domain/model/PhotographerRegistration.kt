package com.gndy.camman.domain.model

import kotlinx.serialization.Serializable

/**
 * Photography specialties available for selection
 */
enum class PhotographySpecialty(val displayName: String) {
    WEDDING("Wedding"),
    PORTRAIT("Portrait"),
    EVENT("Event"),
    FASHION("Fashion"),
    PRODUCT("Product"),
    LANDSCAPE("Landscape"),
    SPORTS("Sports"),
    FAMILY("Family"),
    NEWBORN("Newborn"),
    CORPORATE("Corporate"),
    REAL_ESTATE("Real Estate"),
    FOOD("Food"),
    TRAVEL("Travel"),
    WILDLIFE("Wildlife")
}

/**
 * Experience level of the photographer
 */
enum class ExperienceLevel(val displayName: String, val yearsRange: String) {
    BEGINNER("Beginner", "0-2 years"),
    INTERMEDIATE("Intermediate", "2-5 years"),
    ADVANCED("Advanced", "5-10 years"),
    EXPERT("Expert", "10+ years")
}

/**
 * Data class for photographer registration
 */
@Serializable
data class PhotographerRegistration(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val location: String = "",
    val specialties: List<String> = emptyList(),
    val experienceLevel: String = ExperienceLevel.BEGINNER.name,
    val yearsOfExperience: Int = 0,
    val profileImageUrl: String? = null,
    val coverImageUrl: String? = null,
    val portfolioUrls: List<String> = emptyList(),
    val startingPrice: Double = 0.0,
    val currency: String = "USD",
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null,
    val isAvailable: Boolean = true,
    val isProfileComplete: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

/**
 * Registration step tracking
 */
enum class RegistrationStep {
    BASIC_INFO,      // Name, email, phone
    PROFESSIONAL,    // Bio, specialties, experience
    PORTFOLIO,       // Profile image, cover, portfolio samples
    PRICING,         // Starting price, availability
    REVIEW           // Final review before submission
}
