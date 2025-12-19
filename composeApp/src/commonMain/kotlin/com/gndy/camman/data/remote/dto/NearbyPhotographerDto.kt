package com.gndy.camman.data.remote.dto

import com.gndy.camman.domain.model.AvailabilityStatus
import com.gndy.camman.domain.model.NearbyPhotographer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearbyPhotographersResponse(
    val photographers: List<NearbyPhotographerDto>,
    val totalCount: Int = 0,
    val timestamp: Long = 0L
)

@Serializable
data class NearbyPhotographerDto(
    val id: String,
    val name: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,
    @SerialName("cover_image_url")
    val coverImageUrl: String? = null,
    val bio: String? = null,
    val specialties: List<String> = emptyList(),
    val location: String? = null,
    val latitude: Double,
    val longitude: Double,
    val rating: Float = 0f,
    @SerialName("review_count")
    val reviewCount: Int = 0,
    @SerialName("starting_price")
    val startingPrice: Double? = null,
    @SerialName("max_price")
    val maxPrice: Double? = null,
    val currency: String = "USD",
    @SerialName("is_available")
    val isAvailable: Boolean = true,
    @SerialName("portfolio_preview_urls")
    val portfolioPreviewUrls: List<String> = emptyList(),
    @SerialName("distance_km")
    val distanceKm: Double = 0.0,
    @SerialName("estimated_travel_time")
    val estimatedTravelTime: String? = null,
    @SerialName("availability_status")
    val availabilityStatus: String = "AVAILABLE",
    @SerialName("response_time")
    val responseTime: String? = null
) {
    fun toNearbyPhotographer(): NearbyPhotographer {
        return NearbyPhotographer(
            id = id,
            name = name,
            profileImageUrl = profileImageUrl,
            coverImageUrl = coverImageUrl,
            bio = bio,
            specialties = specialties,
            location = location,
            latitude = latitude,
            longitude = longitude,
            rating = rating,
            reviewCount = reviewCount,
            startingPrice = startingPrice,
            maxPrice = maxPrice,
            currency = currency,
            isAvailable = isAvailable,
            portfolioPreviewUrls = portfolioPreviewUrls,
            distanceKm = distanceKm,
            estimatedTravelTime = estimatedTravelTime,
            availabilityStatus = try {
                AvailabilityStatus.valueOf(availabilityStatus)
            } catch (e: Exception) {
                AvailabilityStatus.AVAILABLE
            },
            responseTime = responseTime
        )
    }
}

@Serializable
data class PhotographerDistanceResponse(
    @SerialName("photographer_id")
    val photographerId: String,
    @SerialName("distance_km")
    val distanceKm: Double,
    @SerialName("estimated_travel_time")
    val estimatedTravelTime: String? = null
)
