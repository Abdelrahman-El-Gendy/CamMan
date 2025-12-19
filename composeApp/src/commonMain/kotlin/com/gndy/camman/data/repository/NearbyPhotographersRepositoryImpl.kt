package com.gndy.camman.data.repository

import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.domain.model.AvailabilityStatus
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.PhotographyType
import com.gndy.camman.domain.model.PriceRange
import com.gndy.camman.domain.repository.NearbyPhotographersRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class NearbyPhotographersRepositoryImpl(
    private val apiService: CamManApiService
) : NearbyPhotographersRepository {

    override fun getNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int,
        photographyType: PhotographyType?,
        priceRange: PriceRange?,
        availableOnly: Boolean
    ): Flow<Resource<List<NearbyPhotographer>>> = flow {
        emit(Resource.Loading())
        
        try {
            val response = apiService.getNearbyPhotographers(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm,
                specialty = photographyType?.displayName?.takeIf { it != "All" },
                availableOnly = availableOnly
            )
            
            var photographers = response.photographers.map { it.toNearbyPhotographer() }
            
            // Apply price range filter locally if specified
            if (priceRange != null && priceRange != PriceRange.ANY) {
                photographers = photographers.filter { photographer ->
                    photographer.startingPrice?.let { price ->
                        price >= priceRange.minPrice && price <= priceRange.maxPrice
                    } ?: false
                }
            }
            
            // Sort by distance
            photographers = photographers.sortedBy { it.distanceKm }
            
            emit(Resource.Success(photographers))
        } catch (e: Exception) {
            // Fall back to mock data for development
            val mockData = getMockNearbyPhotographers(latitude, longitude, radiusKm)
                .let { photographers ->
                    var filtered = photographers
                    
                    // Filter by photography type
                    if (photographyType != null && photographyType != PhotographyType.ALL) {
                        filtered = filtered.filter { photographer ->
                            photographer.specialties.any { 
                                it.equals(photographyType.displayName, ignoreCase = true) 
                            }
                        }
                    }
                    
                    // Filter by price range
                    if (priceRange != null && priceRange != PriceRange.ANY) {
                        filtered = filtered.filter { photographer ->
                            photographer.startingPrice?.let { price ->
                                price >= priceRange.minPrice && price <= priceRange.maxPrice
                            } ?: false
                        }
                    }
                    
                    // Filter by availability
                    if (availableOnly) {
                        filtered = filtered.filter { it.isAvailable }
                    }
                    
                    filtered.sortedBy { it.distanceKm }
                }
            
            emit(Resource.Success(mockData))
        }
    }

    override suspend fun getPhotographerWithDistance(
        photographerId: String,
        userLatitude: Double,
        userLongitude: Double
    ): Resource<NearbyPhotographer> {
        return try {
            val response = apiService.getPhotographerWithDistance(
                photographerId = photographerId,
                latitude = userLatitude,
                longitude = userLongitude
            )
            Resource.Success(response.toNearbyPhotographer())
        } catch (e: Exception) {
            // Fallback to mock
            val mockPhotographer = getMockNearbyPhotographers(userLatitude, userLongitude, 50)
                .find { it.id == photographerId }
            
            if (mockPhotographer != null) {
                Resource.Success(mockPhotographer)
            } else {
                Resource.Error(e.message ?: "Photographer not found")
            }
        }
    }

    override suspend fun refreshNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int
    ): Resource<Unit> {
        return try {
            apiService.getNearbyPhotographers(latitude, longitude, radiusKm)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh nearby photographers")
        }
    }

    /**
     * Convert degrees to radians
     */
    private fun toRadians(degrees: Double): Double = degrees * PI / 180.0

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2).pow(2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadiusKm * c
    }
    
    /**
     * Round to one decimal place
     */
    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10).roundToInt() / 10.0
    }

    /**
     * Mock data for development/testing
     */
    private fun getMockNearbyPhotographers(
        userLat: Double,
        userLon: Double,
        radiusKm: Int
    ): List<NearbyPhotographer> {
        // Sample photographers with varied locations around the user
        val mockPhotographers = listOf(
            NearbyPhotographer(
                id = "nearby_1",
                name = "Alex Rivera",
                profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800",
                bio = "Award-winning wedding photographer with 10+ years of experience capturing timeless moments.",
                specialties = listOf("Wedding", "Portrait", "Events"),
                location = "Downtown",
                latitude = userLat + 0.02,
                longitude = userLon + 0.015,
                rating = 4.9f,
                reviewCount = 127,
                startingPrice = 299.0,
                maxPrice = 1500.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1519741497674-611481863552?w=400",
                    "https://images.unsplash.com/photo-1606216794074-735e91aa2c92?w=400"
                ),
                distanceKm = 2.5,
                estimatedTravelTime = "8 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 1 hour"
            ),
            NearbyPhotographer(
                id = "nearby_2",
                name = "Sarah Chen",
                profileImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=800",
                bio = "Passionate portrait photographer specializing in natural light and candid shots.",
                specialties = listOf("Portrait", "Family", "Headshots"),
                location = "Midtown",
                latitude = userLat - 0.01,
                longitude = userLon + 0.025,
                rating = 4.8f,
                reviewCount = 89,
                startingPrice = 199.0,
                maxPrice = 800.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
                ),
                distanceKm = 3.2,
                estimatedTravelTime = "12 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 30 minutes"
            ),
            NearbyPhotographer(
                id = "nearby_3",
                name = "Marcus Johnson",
                profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
                bio = "Corporate and event photographer. Making your business stand out with professional imagery.",
                specialties = listOf("Corporate", "Events", "Product"),
                location = "Business District",
                latitude = userLat + 0.035,
                longitude = userLon - 0.01,
                rating = 4.7f,
                reviewCount = 64,
                startingPrice = 350.0,
                maxPrice = 2000.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?w=400",
                    "https://images.unsplash.com/photo-1511578314322-379afb476865?w=400"
                ),
                distanceKm = 4.1,
                estimatedTravelTime = "15 mins",
                availabilityStatus = AvailabilityStatus.BUSY,
                responseTime = "Usually responds within 2 hours"
            ),
            NearbyPhotographer(
                id = "nearby_4",
                name = "Emma Williams",
                profileImageUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1537633552985-df8429e8048b?w=800",
                bio = "Capturing the magic of newborns and families. Gentle, patient, and creative approach.",
                specialties = listOf("Newborn", "Family", "Maternity"),
                location = "Suburbs",
                latitude = userLat - 0.04,
                longitude = userLon - 0.02,
                rating = 5.0f,
                reviewCount = 156,
                startingPrice = 249.0,
                maxPrice = 900.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1519689680058-324335c77eba?w=400",
                    "https://images.unsplash.com/photo-1544126592-807ade215a0b?w=400"
                ),
                distanceKm = 5.8,
                estimatedTravelTime = "20 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 1 hour"
            ),
            NearbyPhotographer(
                id = "nearby_5",
                name = "David Park",
                profileImageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800",
                bio = "Real estate and architectural photographer. Making properties shine!",
                specialties = listOf("Real Estate", "Architecture", "Interior"),
                location = "East Side",
                latitude = userLat + 0.05,
                longitude = userLon + 0.04,
                rating = 4.6f,
                reviewCount = 43,
                startingPrice = 175.0,
                maxPrice = 600.0,
                currency = "USD",
                isAvailable = false,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=400",
                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400"
                ),
                distanceKm = 7.2,
                estimatedTravelTime = "25 mins",
                availabilityStatus = AvailabilityStatus.AWAY,
                responseTime = "Usually responds within 4 hours"
            ),
            NearbyPhotographer(
                id = "nearby_6",
                name = "Lisa Thompson",
                profileImageUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1469371670807-013ccf25f16a?w=800",
                bio = "Fashion and editorial photographer with work featured in major publications.",
                specialties = listOf("Fashion", "Editorial", "Portrait"),
                location = "Fashion District",
                latitude = userLat - 0.03,
                longitude = userLon + 0.05,
                rating = 4.9f,
                reviewCount = 201,
                startingPrice = 450.0,
                maxPrice = 3000.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=400",
                    "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=400"
                ),
                distanceKm = 6.5,
                estimatedTravelTime = "22 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 2 hours"
            ),
            NearbyPhotographer(
                id = "nearby_7",
                name = "James Wilson",
                profileImageUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800",
                bio = "Sports and action photographer. Capturing the thrill of the moment.",
                specialties = listOf("Sports", "Action", "Events"),
                location = "Sports Complex",
                latitude = userLat + 0.06,
                longitude = userLon - 0.03,
                rating = 4.7f,
                reviewCount = 78,
                startingPrice = 275.0,
                maxPrice = 1200.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=400",
                    "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=400"
                ),
                distanceKm = 8.9,
                estimatedTravelTime = "28 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 3 hours"
            ),
            NearbyPhotographer(
                id = "nearby_8",
                name = "Maria Garcia",
                profileImageUrl = "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800",
                bio = "Food and culinary photographer making dishes look irresistible.",
                specialties = listOf("Food", "Product", "Lifestyle"),
                location = "Culinary District",
                latitude = userLat - 0.02,
                longitude = userLon - 0.04,
                rating = 4.8f,
                reviewCount = 92,
                startingPrice = 225.0,
                maxPrice = 850.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=400",
                    "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400"
                ),
                distanceKm = 4.8,
                estimatedTravelTime = "16 mins",
                availabilityStatus = AvailabilityStatus.AVAILABLE,
                responseTime = "Usually responds within 1 hour"
            )
        )

        // Calculate actual distances and filter by radius
        return mockPhotographers.map { photographer ->
            val distance = calculateDistance(userLat, userLon, photographer.latitude, photographer.longitude)
            photographer.copy(
                distanceKm = roundToOneDecimal(distance),
                estimatedTravelTime = "${(distance * 4).toInt()} mins" // Approximate travel time
            )
        }.filter { it.distanceKm <= radiusKm }
    }
}
