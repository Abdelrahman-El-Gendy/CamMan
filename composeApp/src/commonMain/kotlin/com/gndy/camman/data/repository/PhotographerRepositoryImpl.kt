package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.PhotographerRegistrationDao
import com.gndy.camman.data.local.entity.PhotographerRegistrationEntity
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.model.PhotographerRegistration
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock

/**
 * Repository implementation for photographer data
 * Uses Room database for persistence
 */
class PhotographerRepositoryImpl(
    private val photographerRegistrationDao: PhotographerRegistrationDao
) : PhotographerRepository {

    // Cache for current profile to trigger updates
    private val _profileUpdateTrigger = MutableStateFlow(0L)

    override fun getAllPhotographers(): Flow<Resource<List<Photographer>>> =
        photographerRegistrationDao.getAllRegisteredPhotographers()
            .map { entities ->
                val registeredPhotographers =
                    entities.map { it.toPhotographerRegistration().toPhotographer() }
                val allPhotographers = getMockPhotographers() + registeredPhotographers
                Resource.Success(allPhotographers) as Resource<List<Photographer>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to load photographers")) }

    override suspend fun getPhotographerById(id: String): Resource<Photographer> {
        return try {
            // Check database first
            val entity = photographerRegistrationDao.getPhotographerById(id).first()
            if (entity != null) {
                return Resource.Success(entity.toPhotographerRegistration().toPhotographer())
            }

            // Then check mock data
            val mock = getMockPhotographers().find { it.id == id }
            if (mock != null) {
                Resource.Success(mock)
            } else {
                Resource.Error("Photographer not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get photographer")
        }
    }

    override fun getCurrentPhotographerProfile(): Flow<Resource<PhotographerRegistration?>> =
        photographerRegistrationDao.getCurrentPhotographerProfile()
            .map { entity ->
                Resource.Success(entity?.toPhotographerRegistration()) as Resource<PhotographerRegistration?>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to get profile")) }

    override suspend fun hasCompletedRegistration(): Boolean {
        return try {
            photographerRegistrationDao.hasCompletedRegistration()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun savePhotographerRegistration(
        registration: PhotographerRegistration
    ): Resource<PhotographerRegistration> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val updatedRegistration = registration.copy(
                id = if (registration.id.isEmpty()) generateId() else registration.id,
                updatedAt = timestamp
            )

            // Save to Room database
            val entity =
                PhotographerRegistrationEntity.fromPhotographerRegistration(updatedRegistration)
            photographerRegistrationDao.insertOrUpdate(entity)

            // Trigger update for observers
            _profileUpdateTrigger.value = timestamp

            Resource.Success(updatedRegistration)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save registration")
        }
    }

    override suspend fun updatePhotographerProfile(
        registration: PhotographerRegistration
    ): Resource<Unit> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val updatedRegistration = registration.copy(updatedAt = timestamp)

            val entity =
                PhotographerRegistrationEntity.fromPhotographerRegistration(updatedRegistration)
            photographerRegistrationDao.insertOrUpdate(entity)

            // Trigger update for observers
            _profileUpdateTrigger.value = timestamp

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun updateAvailability(isAvailable: Boolean): Resource<Unit> {
        return try {
            val currentProfile = photographerRegistrationDao.getCurrentPhotographerProfile().first()
            currentProfile?.let { entity ->
                val timestamp = Clock.System.now().toEpochMilliseconds()
                photographerRegistrationDao.updateAvailability(entity.id, isAvailable, timestamp)

                // Trigger update for observers
                _profileUpdateTrigger.value = timestamp
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update availability")
        }
    }

    override fun searchPhotographers(query: String): Flow<Resource<List<Photographer>>> =
        photographerRegistrationDao.searchPhotographers(query)
            .map { entities ->
                val registeredPhotographers = entities
                    .filter { it.isProfileComplete }
                    .map { it.toPhotographerRegistration().toPhotographer() }

                val mockMatches = getMockPhotographers().filter { photographer ->
                    photographer.name.contains(query, ignoreCase = true) ||
                            photographer.location?.contains(query, ignoreCase = true) == true ||
                            photographer.specialties.any { it.contains(query, ignoreCase = true) }
                }

                Resource.Success(mockMatches + registeredPhotographers) as Resource<List<Photographer>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Search failed")) }

    override fun filterBySpecialty(specialty: String): Flow<Resource<List<Photographer>>> =
        photographerRegistrationDao.filterBySpecialty(specialty)
            .map { entities ->
                val registeredPhotographers =
                    entities.map { it.toPhotographerRegistration().toPhotographer() }

                val mockMatches = getMockPhotographers().filter { photographer ->
                    photographer.specialties.any { it.equals(specialty, ignoreCase = true) }
                }

                Resource.Success(mockMatches + registeredPhotographers) as Resource<List<Photographer>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Filter failed")) }

    private fun generateId(): String {
        return "photographer_${Clock.System.now().toEpochMilliseconds()}"
    }

    private fun PhotographerRegistration.toPhotographer(): Photographer {
        return Photographer(
            id = id,
            name = fullName,
            profileImageUrl = profileImageUrl,
            coverImageUrl = coverImageUrl,
            bio = bio.ifEmpty { null },
            specialties = specialties,
            location = location.ifEmpty { null },
            rating = 0f, // New photographer starts with no rating
            reviewCount = 0,
            startingPrice = if (startingPrice > 0) startingPrice else null,
            currency = currency,
            isAvailable = isAvailable,
            portfolioPreviewUrls = portfolioUrls
        )
    }

    private fun getMockPhotographers(): List<Photographer> {
        return listOf(
            Photographer(
                id = "mock_1",
                name = "Alex Rivera",
                profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800",
                bio = "Award-winning wedding and portrait photographer with 10+ years of experience.",
                specialties = listOf("Wedding", "Portrait", "Event"),
                location = "New York, NY",
                rating = 4.9f,
                reviewCount = 127,
                startingPrice = 199.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1519741497674-611481863552?w=400",
                    "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400",
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400"
                )
            ),
            Photographer(
                id = "mock_2",
                name = "Sarah Chen",
                profileImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=800",
                bio = "Fashion and editorial photographer specializing in high-end commercial work.",
                specialties = listOf("Fashion", "Portrait", "Product"),
                location = "Los Angeles, CA",
                rating = 4.8f,
                reviewCount = 89,
                startingPrice = 299.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=400",
                    "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=400",
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"
                )
            ),
            Photographer(
                id = "mock_3",
                name = "Marcus Johnson",
                profileImageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
                bio = "Corporate events and headshot specialist. Making professionals look their best.",
                specialties = listOf("Event", "Portrait", "Corporate"),
                location = "Chicago, IL",
                rating = 4.7f,
                reviewCount = 156,
                startingPrice = 149.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400",
                    "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=400",
                    "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400"
                )
            ),
            Photographer(
                id = "mock_4",
                name = "Emma Wilson",
                profileImageUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=800",
                bio = "Capturing love stories through my lens. Destination wedding photographer.",
                specialties = listOf("Wedding", "Engagement", "Couples"),
                location = "Miami, FL",
                rating = 5.0f,
                reviewCount = 73,
                startingPrice = 349.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1519741497674-611481863552?w=400",
                    "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=400",
                    "https://images.unsplash.com/photo-1460978812857-470ed1c77af0?w=400"
                )
            ),
            Photographer(
                id = "mock_5",
                name = "David Park",
                profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                bio = "Nature and landscape photographer. Let's capture the beauty of the outdoors.",
                specialties = listOf("Landscape", "Nature", "Travel"),
                location = "Denver, CO",
                rating = 4.6f,
                reviewCount = 98,
                startingPrice = 179.0,
                currency = "USD",
                isAvailable = false,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400",
                    "https://images.unsplash.com/photo-1433086966358-54859d0ed716?w=400"
                )
            ),
            Photographer(
                id = "mock_6",
                name = "Lisa Thompson",
                profileImageUrl = "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400",
                coverImageUrl = "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=800",
                bio = "Family and newborn photographer. Creating memories that last generations.",
                specialties = listOf("Family", "Newborn", "Portrait"),
                location = "Austin, TX",
                rating = 4.9f,
                reviewCount = 201,
                startingPrice = 249.0,
                currency = "USD",
                isAvailable = true,
                portfolioPreviewUrls = listOf(
                    "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=400",
                    "https://images.unsplash.com/photo-1476703993599-0035a21b17a9?w=400",
                    "https://images.unsplash.com/photo-1491013516836-7db643ee125a?w=400"
                )
            )
        )
    }
}
