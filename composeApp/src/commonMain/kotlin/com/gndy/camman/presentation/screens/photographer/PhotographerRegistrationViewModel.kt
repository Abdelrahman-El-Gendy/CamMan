package com.gndy.camman.presentation.screens.photographer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.ExperienceLevel
import com.gndy.camman.domain.model.PhotographerRegistration
import com.gndy.camman.domain.model.PhotographySpecialty
import com.gndy.camman.domain.model.RegistrationStep
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

data class PhotographerRegistrationUiState(
    val currentStep: RegistrationStep = RegistrationStep.BASIC_INFO,
    val isLoading: Boolean = false,
    val hasExistingProfile: Boolean = false,

    // Basic Info
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",

    // Professional Info
    val bio: String = "",
    val location: String = "",
    val selectedSpecialties: Set<String> = emptySet(),
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val yearsOfExperience: Int = 0,

    // Portfolio
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

sealed class PhotographerRegistrationEvent {
    data object RegistrationSuccess : PhotographerRegistrationEvent()
    data class ShowError(val message: String) : PhotographerRegistrationEvent()
    data object NavigateToDashboard : PhotographerRegistrationEvent()
}

class PhotographerRegistrationViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotographerRegistrationUiState())
    val uiState: StateFlow<PhotographerRegistrationUiState> = _uiState.asStateFlow()

    // Use replay = 1 to ensure event is delivered even if collector starts late
    private val _events = MutableSharedFlow<PhotographerRegistrationEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    // Flag to prevent re-checking profile after successful registration
    private var registrationCompleted = false

    init {
        checkExistingProfile()
    }

    private fun checkExistingProfile() {
        if (registrationCompleted) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Skip Loading state and get the actual result
                val resource = photographerRepository.getCurrentPhotographerProfile()
                    .first { it !is Resource.Loading }

                when (resource) {
                    is Resource.Success<*> -> {
                        val profile = resource.data as? PhotographerRegistration
                        if (profile != null && profile.isProfileComplete) {
                            _uiState.update {
                                it.copy(
                                    hasExistingProfile = true,
                                    isLoading = false
                                )
                            }
                            _events.emit(PhotographerRegistrationEvent.NavigateToDashboard)
                        } else if (profile != null) {
                            // Resume incomplete registration
                            loadProfileIntoState(profile)
                        } else {
                            // No existing profile, start fresh
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }

                    is Resource.Error<*> -> {
                        // No existing profile, start fresh
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    else -> {
                        // Shouldn't happen, but handle gracefully
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                // No existing profile, start fresh
                _uiState.update { it.copy(isLoading = false) }
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
                yearsOfExperience = profile.yearsOfExperience,
                profileImageUrl = profile.profileImageUrl ?: "",
                coverImageUrl = profile.coverImageUrl ?: "",
                portfolioUrls = profile.portfolioUrls,
                startingPrice = if (profile.startingPrice > 0) profile.startingPrice.toString() else "",
                currency = profile.currency,
                website = profile.website ?: "",
                instagram = profile.instagram ?: "",
                facebook = profile.facebook ?: "",
                isAvailable = profile.isAvailable,
                isLoading = false
            )
        }
    }

    // ============ Basic Info Updates ============

    fun onFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                fullName = name,
                errors = it.errors - "fullName"
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errors = it.errors - "email"
            )
        }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update {
            it.copy(
                phoneNumber = phone,
                errors = it.errors - "phone"
            )
        }
    }

    // ============ Professional Info Updates ============

    fun onBioChanged(bio: String) {
        _uiState.update {
            it.copy(
                bio = bio,
                errors = it.errors - "bio"
            )
        }
    }

    fun onLocationChanged(location: String) {
        _uiState.update {
            it.copy(
                location = location,
                errors = it.errors - "location"
            )
        }
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
    }

    fun onExperienceLevelSelected(level: ExperienceLevel) {
        _uiState.update {
            it.copy(
                experienceLevel = level,
                yearsOfExperience = when (level) {
                    ExperienceLevel.BEGINNER -> 1
                    ExperienceLevel.INTERMEDIATE -> 3
                    ExperienceLevel.ADVANCED -> 7
                    ExperienceLevel.EXPERT -> 12
                }
            )
        }
    }

    // ============ Portfolio Updates ============

    fun onProfileImageUrlChanged(url: String) {
        _uiState.update { it.copy(profileImageUrl = url) }
    }

    fun onCoverImageUrlChanged(url: String) {
        _uiState.update { it.copy(coverImageUrl = url) }
    }

    fun onPortfolioUrlAdded(url: String) {
        if (url.isNotBlank()) {
            _uiState.update { state ->
                state.copy(portfolioUrls = state.portfolioUrls + url)
            }
        }
    }

    fun onPortfolioUrlRemoved(index: Int) {
        _uiState.update { state ->
            state.copy(portfolioUrls = state.portfolioUrls.filterIndexed { i, _ -> i != index })
        }
    }

    // ============ Pricing Updates ============

    fun onStartingPriceChanged(price: String) {
        // Only allow numeric input
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(startingPrice = price) }
        }
    }

    fun onCurrencySelected(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }

    fun onWebsiteChanged(website: String) {
        _uiState.update { it.copy(website = website) }
    }

    fun onInstagramChanged(instagram: String) {
        _uiState.update { it.copy(instagram = instagram) }
    }

    fun onFacebookChanged(facebook: String) {
        _uiState.update { it.copy(facebook = facebook) }
    }

    fun onAvailabilityChanged(isAvailable: Boolean) {
        _uiState.update { it.copy(isAvailable = isAvailable) }
    }

    // ============ Navigation ============

    fun onNextStep() {
        val state = _uiState.value

        // Validate current step
        val errors = validateCurrentStep(state)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        // Move to next step
        val nextStep = when (state.currentStep) {
            RegistrationStep.BASIC_INFO -> RegistrationStep.PROFESSIONAL
            RegistrationStep.PROFESSIONAL -> RegistrationStep.PORTFOLIO
            RegistrationStep.PORTFOLIO -> RegistrationStep.PRICING
            RegistrationStep.PRICING -> RegistrationStep.REVIEW
            RegistrationStep.REVIEW -> {
                submitRegistration()
                return
            }
        }

        _uiState.update { it.copy(currentStep = nextStep) }
    }

    fun onPreviousStep() {
        val state = _uiState.value

        val previousStep = when (state.currentStep) {
            RegistrationStep.BASIC_INFO -> return // Can't go back from first step
            RegistrationStep.PROFESSIONAL -> RegistrationStep.BASIC_INFO
            RegistrationStep.PORTFOLIO -> RegistrationStep.PROFESSIONAL
            RegistrationStep.PRICING -> RegistrationStep.PORTFOLIO
            RegistrationStep.REVIEW -> RegistrationStep.PRICING
        }

        _uiState.update { it.copy(currentStep = previousStep) }
    }

    fun goToStep(step: RegistrationStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    private fun validateCurrentStep(state: PhotographerRegistrationUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        when (state.currentStep) {
            RegistrationStep.BASIC_INFO -> {
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
            }

            RegistrationStep.PROFESSIONAL -> {
                if (state.bio.length < 20) {
                    errors["bio"] = "Bio should be at least 20 characters"
                }
                if (state.location.isBlank()) {
                    errors["location"] = "Location is required"
                }
                if (state.selectedSpecialties.isEmpty()) {
                    errors["specialties"] = "Select at least one specialty"
                }
            }

            RegistrationStep.PORTFOLIO -> {
                // Portfolio images are optional during registration
            }

            RegistrationStep.PRICING -> {
                // Pricing is optional
            }

            RegistrationStep.REVIEW -> {
                // Final validation before submission
            }
        }

        return errors
    }

    private fun submitRegistration() {
        // Prevent double submission
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val state = _uiState.value
                val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                val registration = PhotographerRegistration(
                    fullName = state.fullName,
                    email = state.email,
                    phoneNumber = state.phoneNumber,
                    bio = state.bio,
                    location = state.location,
                    specialties = state.selectedSpecialties.toList(),
                    experienceLevel = state.experienceLevel.name,
                    yearsOfExperience = state.yearsOfExperience,
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
                    createdAt = now,
                    updatedAt = now
                )

                val result = photographerRepository.savePhotographerRegistration(registration)

                when (result) {
                    is Resource.Success<*> -> {
                        // Set flag to prevent re-checking profile
                        registrationCompleted = true
                        _uiState.update { it.copy(isLoading = false, hasExistingProfile = true) }
                        // Use tryEmit to ensure event is sent
                        _events.tryEmit(PhotographerRegistrationEvent.RegistrationSuccess)
                    }

                    is Resource.Error<*> -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.tryEmit(
                            PhotographerRegistrationEvent.ShowError(
                                result.message ?: "Registration failed"
                            )
                        )
                    }

                    is Resource.Loading<*> -> {
                        // Shouldn't happen for suspend function
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.tryEmit(
                    PhotographerRegistrationEvent.ShowError(
                        e.message ?: "Registration failed"
                    )
                )
            }
        }
    }

    fun getStepProgress(): Float {
        return when (_uiState.value.currentStep) {
            RegistrationStep.BASIC_INFO -> 0.2f
            RegistrationStep.PROFESSIONAL -> 0.4f
            RegistrationStep.PORTFOLIO -> 0.6f
            RegistrationStep.PRICING -> 0.8f
            RegistrationStep.REVIEW -> 1.0f
        }
    }

    fun getStepNumber(): Int {
        return when (_uiState.value.currentStep) {
            RegistrationStep.BASIC_INFO -> 1
            RegistrationStep.PROFESSIONAL -> 2
            RegistrationStep.PORTFOLIO -> 3
            RegistrationStep.PRICING -> 4
            RegistrationStep.REVIEW -> 5
        }
    }
}
