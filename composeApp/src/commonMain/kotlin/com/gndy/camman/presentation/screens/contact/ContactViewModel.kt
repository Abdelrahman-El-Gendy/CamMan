package com.gndy.camman.presentation.screens.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.model.SocialLinks
import com.gndy.camman.domain.usecase.profile.GetPhotographerContactInfoUseCase
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactUiState(
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val profile: PhotographerProfile? = null,

    // Contact form
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val message: String = "",

    val error: String? = null
)

sealed class ContactUiEvent {
    data object MessageSent : ContactUiEvent()
    data class ShowError(val message: String) : ContactUiEvent()
    data class OpenEmail(val email: String) : ContactUiEvent()
    data class OpenPhone(val phone: String) : ContactUiEvent()
    data class OpenUrl(val url: String) : ContactUiEvent()
}

class ContactViewModel(
    private val getPhotographerContactInfoUseCase: GetPhotographerContactInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ContactUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ===== MOCK DATA - Comment this block when backend is ready =====
            delay(500) // Simulate brief loading
            val profile = getMockProfile()
            _uiState.update {
                it.copy(
                    profile = profile,
                    isLoading = false
                )
            }
            // ===== END MOCK DATA =====

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                getPhotographerContactInfoUseCase().collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    profile = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
            // ===== END REAL IMPLEMENTATION =====
            */
        }
    }

    private fun getMockProfile(): PhotographerProfile {
        return PhotographerProfile(
            id = "1",
            name = "Alex Rivera",
            bio = "Professional photographer with 10+ years of experience specializing in weddings, portraits, and events. Let me capture your precious moments.",
            profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            coverImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=1200",
            specialties = listOf("Wedding", "Portrait", "Event", "Landscape"),
            yearsOfExperience = 10,
            location = "New York, NY",
            email = "alex@camman.com",
            phone = "+1 (555) 123-4567",
            website = "https://camman.com",
            socialLinks = SocialLinks(
                instagram = "https://instagram.com/alexrivera",
                facebook = "https://facebook.com/alexrivera",
                twitter = "https://twitter.com/alexrivera",
                linkedin = null,
                youtube = null
            ),
            rating = 4.9f,
            reviewCount = 127
        )
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSubjectChanged(subject: String) {
        _uiState.update { it.copy(subject = subject) }
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun sendMessage() {
        val state = _uiState.value

        if (state.name.isBlank() || state.email.isBlank() || state.message.isBlank()) {
            viewModelScope.launch {
                _uiEvents.emit(ContactUiEvent.ShowError("Please fill in all required fields"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            // ===== MOCK - Simulate sending message =====
            try {
                delay(1500) // Simulate network delay

                _uiState.update {
                    it.copy(
                        isSending = false,
                        name = "",
                        email = "",
                        subject = "",
                        message = ""
                    )
                }
                _uiEvents.emit(ContactUiEvent.MessageSent)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSending = false) }
                _uiEvents.emit(ContactUiEvent.ShowError("Failed to send message"))
            }
            // ===== END MOCK =====

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                // Call actual API to send message
                // contactRepository.sendMessage(state.name, state.email, state.subject, state.message)
                
                _uiState.update {
                    it.copy(
                        isSending = false,
                        name = "",
                        email = "",
                        subject = "",
                        message = ""
                    )
                }
                _uiEvents.emit(ContactUiEvent.MessageSent)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSending = false) }
                _uiEvents.emit(ContactUiEvent.ShowError(e.message ?: "Failed to send message"))
            }
            // ===== END REAL IMPLEMENTATION =====
            */
        }
    }

    fun onEmailClick() {
        viewModelScope.launch {
            _uiState.value.profile?.email?.let { email ->
                _uiEvents.emit(ContactUiEvent.OpenEmail(email))
            }
        }
    }

    fun onPhoneClick() {
        viewModelScope.launch {
            _uiState.value.profile?.phone?.let { phone ->
                _uiEvents.emit(ContactUiEvent.OpenPhone(phone))
            }
        }
    }

    fun onWebsiteClick() {
        viewModelScope.launch {
            _uiState.value.profile?.website?.let { url ->
                _uiEvents.emit(ContactUiEvent.OpenUrl(url))
            }
        }
    }

    fun onSocialLinkClick(url: String) {
        viewModelScope.launch {
            _uiEvents.emit(ContactUiEvent.OpenUrl(url))
        }
    }
}
