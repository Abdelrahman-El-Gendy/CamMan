package com.gndy.camman.domain.model

/**
 * Extended photographer model with location-based data for nearby feature
 */
data class NearbyPhotographer(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val bio: String?,
    val specialties: List<String>,
    val location: String?,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val reviewCount: Int,
    val startingPrice: Double?,
    val maxPrice: Double? = null,
    val currency: String = "USD",
    val isAvailable: Boolean = true,
    val portfolioPreviewUrls: List<String> = emptyList(),
    val distanceKm: Double,
    val estimatedTravelTime: String? = null,
    val availabilityStatus: AvailabilityStatus = AvailabilityStatus.AVAILABLE,
    val responseTime: String? = null // e.g., "Usually responds within 1 hour"
)

/**
 * User location model
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f
)

/**
 * Photography type/specialty enum for filtering
 */
enum class PhotographyType(val displayName: String) {
    ALL("All"),
    WEDDING("Wedding"),
    PORTRAIT("Portrait"),
    EVENT("Events"),
    PRODUCT("Product"),
    FASHION("Fashion"),
    LANDSCAPE("Landscape"),
    SPORTS("Sports"),
    FAMILY("Family"),
    NEWBORN("Newborn"),
    REAL_ESTATE("Real Estate"),
    FOOD("Food"),
    CORPORATE("Corporate"),
    HEADSHOTS("Headshots")
}

/**
 * Availability status for photographers
 */
enum class AvailabilityStatus {
    AVAILABLE,
    BUSY,
    AWAY,
    OFFLINE
}

/**
 * Price range filter options
 */
enum class PriceRange(val displayName: String, val minPrice: Double, val maxPrice: Double) {
    ANY("Any Price", 0.0, Double.MAX_VALUE),
    BUDGET("Budget ($0-$100)", 0.0, 100.0),
    MODERATE("Moderate ($100-$300)", 100.0, 300.0),
    PREMIUM("Premium ($300-$500)", 300.0, 500.0),
    LUXURY("Luxury ($500+)", 500.0, Double.MAX_VALUE)
}

/**
 * Distance radius options for filtering
 */
enum class DistanceRadius(val displayName: String, val radiusKm: Int) {
    NEARBY("5 km", 5),
    CLOSE("10 km", 10),
    MODERATE("25 km", 25),
    FAR("50 km", 50),
    VERY_FAR("100 km", 100)
}
