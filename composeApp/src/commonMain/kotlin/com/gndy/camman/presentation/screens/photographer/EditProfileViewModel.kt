package com.gndy.camman.presentation.screens.photographer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.ExperienceLevel
import com.gndy.camman.domain.model.PhotographerRegistration
import com.gndy.camman.domain.model.PhotographySpecialty
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,

    // Basic Info
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",

    // Professional Info
    val bio: String = "",
    val location: String = "",
    val selectedSpecialties: Set<String> = emptySet(),
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,

    // Images - URL only
    val profileImageUrl: String = "",
    val coverImageUrl: String = "",
    val portfolioUrls: List<String> = emptyList(),

    // Pricing
    val startingPrice: String = "",
    val currency: String = "USD",
    val website: String = "",
    val instagram: String = "",
    val facebook: String = "",
    val isAvailable: Boolean = true,

    // Validation
    val errors: Map<String, String> = emptyMap(),

    // Available options
    val availableSpecialties: List<PhotographySpecialty> = PhotographySpecialty.entries,
    val experienceLevels: List<ExperienceLevel> = ExperienceLevel.entries,
    val currencies: List<String> = listOf("USD", "EUR", "GBP", "CAD", "AUD", "EGP")
)

sealed class EditProfileEvent {
    data object SaveSuccess : EditProfileEvent()
    data class ShowError(val message: String) : EditProfileEvent()
    data object NavigateBack : EditProfileEvent()
}

class EditProfileViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // Use extraBufferCapacity to ensure events are delivered even if collector starts late
    private val _events = MutableSharedFlow<EditProfileEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    private var originalProfile: PhotographerRegistration? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Only load the initial profile, skip loading state
                // Don't continuously collect to avoid overriding user edits
                val resource = photographerRepository.getCurrentPhotographerProfile()
                    .first { it !is Resource.Loading }

                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { profile ->
                            originalProfile = profile
                            loadProfileIntoState(profile)
                        }
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.tryEmit(
                            EditProfileEvent.ShowError(
                                resource.message ?: "Failed to load profile"
                            )
                        )
                    }

                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.tryEmit(EditProfileEvent.ShowError(e.message ?: "Failed to load profile"))
            }
        }
    }

    private fun loadProfileIntoState(profile: PhotographerRegistration) {
        _uiState.update { state ->
            state.copy(
                fullName = profile.fullName,
                email = profile.email,
                phoneNumber = profile.phoneNumber,
                bio = profile.bio,
                location = profile.location,
                selectedSpecialties = profile.specialties.toSet(),
                experienceLevel = ExperienceLevel.entries.find {
                    it.name == profile.experienceLevel
                } ?: ExperienceLevel.BEGINNER,
                profileImageUrl = profile.profileImageUrl ?: "",
                coverImageUrl = profile.coverImageUrl ?: "",
                portfolioUrls = profile.portfolioUrls,
                startingPrice = if (profile.startingPrice > 0) profile.startingPrice.toString() else "",
                currency = profile.currency,
                website = profile.website ?: "",
                instagram = profile.instagram ?: "",
                facebook = profile.facebook ?: "",
                isAvailable = profile.isAvailable,
                isLoading = false,
                hasChanges = false
            )
        }
    }

    private fun markAsChanged() {
        _uiState.update { it.copy(hasChanges = true) }
    }

    // ============ Basic Info Updates ============

    fun onFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                fullName = name,
                errors = it.errors - "fullName"
            )
        }
        markAsChanged()
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errors = it.errors - "email"
            )
        }
        markAsChanged()
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update {
            it.copy(
                phoneNumber = phone,
                errors = it.errors - "phone"
            )
        }
        markAsChanged()
    }

    // ============ Professional Info Updates ============

    fun onBioChanged(bio: String) {
        _uiState.update {
            it.copy(
                bio = bio,
                errors = it.errors - "bio"
            )
        }
        markAsChanged()
    }

    fun onLocationChanged(location: String) {
        _uiState.update {
            it.copy(
                location = location,
                errors = it.errors - "location"
            )
        }
        markAsChanged()
    }

    fun onSpecialtyToggled(specialty: String) {
        _uiState.update { state ->
            val newSpecialties = if (state.selectedSpecialties.contains(specialty)) {
                state.selectedSpecialties - specialty
            } else {
                state.selectedSpecialties + specialty
            }
            state.copy(
                selectedSpecialties = newSpecialties,
                errors = state.errors - "specialties"
            )
        }
        markAsChanged()
    }

    fun onExperienceLevelSelected(level: ExperienceLevel) {
        _uiState.update { it.copy(experienceLevel = level) }
        markAsChanged()
    }

    // ============ Image URL Updates ============

    fun onProfileImageUrlChanged(url: String) {
        _uiState.update { it.copy(profileImageUrl = url) }
        markAsChanged()
    }

    fun onCoverImageUrlChanged(url: String) {
        _uiState.update { it.copy(coverImageUrl = url) }
        markAsChanged()
    }

    fun onPortfolioUrlAdded(url: String) {
        if (url.isNotBlank()) {
            _uiState.update { state ->
                state.copy(portfolioUrls = state.portfolioUrls + url)
            }
            markAsChanged()
        }
    }

    fun onPortfolioUrlRemoved(index: Int) {
        _uiState.update { state ->
            state.copy(portfolioUrls = state.portfolioUrls.filterIndexed { i, _ -> i != index })
        }
        markAsChanged()
    }

    fun clearProfileImage() {
        _uiState.update { it.copy(profileImageUrl = "") }
        markAsChanged()
    }

    fun clearCoverImage() {
        _uiState.update { it.copy(coverImageUrl = "") }
        markAsChanged()
    }

    // ============ Pricing Updates ============

    fun onStartingPriceChanged(price: String) {
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(startingPrice = price) }
            markAsChanged()
        }
    }

    fun onCurrencySelected(currency: String) {
        _uiState.update { it.copy(currency = currency) }
        markAsChanged()
    }

    fun onWebsiteChanged(website: String) {
        _uiState.update { it.copy(website = website) }
        markAsChanged()
    }

    fun onInstagramChanged(instagram: String) {
        _uiState.update { it.copy(instagram = instagram) }
        markAsChanged()
    }

    fun onFacebookChanged(facebook: String) {
        _uiState.update { it.copy(facebook = facebook) }
        markAsChanged()
    }

    fun onAvailabilityChanged(isAvailable: Boolean) {
        _uiState.update { it.copy(isAvailable = isAvailable) }
        markAsChanged()
    }

    // ============ Save & Validation ============

    fun saveProfile() {
        val state = _uiState.value

        // Validate
        val errors = validateProfile(state)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val registration = PhotographerRegistration(
                id = originalProfile?.id ?: "",
                fullName = state.fullName,
                email = state.email,
                phoneNumber = state.phoneNumber,
                bio = state.bio,
                location = state.location,
                specialties = state.selectedSpecialties.toList(),
                experienceLevel = state.experienceLevel.name,
                yearsOfExperience = when (state.experienceLevel) {
                    ExperienceLevel.BEGINNER -> 1
                    ExperienceLevel.INTERMEDIATE -> 3
                    ExperienceLevel.ADVANCED -> 7
                    ExperienceLevel.EXPERT -> 12
                },
                profileImageUrl = state.profileImageUrl.ifBlank { null },
                coverImageUrl = state.coverImageUrl.ifBlank { null },
                portfolioUrls = state.portfolioUrls,
                startingPrice = state.startingPrice.toDoubleOrNull() ?: 0.0,
                currency = state.currency,
                website = state.website.ifBlank { null },
                instagram = state.instagram.ifBlank { null },
                facebook = state.facebook.ifBlank { null },
                isAvailable = state.isAvailable,
                isProfileComplete = true,
                createdAt = originalProfile?.createdAt ?: kotlinx.datetime.Clock.System.now()
                    .toEpochMilliseconds(),
                updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )

            when (val result = photographerRepository.savePhotographerRegistration(registration)) {
                is Resource.Success -> {
                    // Update original profile with the saved one
                    originalProfile = registration.copy(
                        id = result.data?.id ?: registration.id,
                        updatedAt = result.data?.updatedAt ?: registration.updatedAt
                    )
                    _uiState.update { it.copy(isSaving = false, hasChanges = false) }
                    // Use tryEmit for reliable delivery
                    _events.tryEmit(EditProfileEvent.SaveSuccess)
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.tryEmit(EditProfileEvent.ShowError(result.message ?: "Failed to save"))
                }

                is Resource.Loading -> {}
            }
        }
    }

    private fun validateProfile(state: EditProfileUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.fullName.isBlank()) {
            errors["fullName"] = "Full name is required"
        }
        if (state.email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!state.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            errors["email"] = "Please enter a valid email"
        }
        if (state.phoneNumber.isBlank()) {
            errors["phone"] = "Phone number is required"
        }
        if (state.bio.length < 20) {
            errors["bio"] = "Bio should be at least 20 characters"
        }
        if (state.location.isBlank()) {
            errors["location"] = "Location is required"
        }
        if (state.selectedSpecialties.isEmpty()) {
            errors["specialties"] = "Select at least one specialty"
        }

        return errors
    }

    fun discardChanges() {
        originalProfile?.let { loadProfileIntoState(it) }
    }
}
