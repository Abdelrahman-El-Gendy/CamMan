package com.gndy.camman.presentation.screens.photographer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.PhotographerRegistration
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotographerMainUiState(
    val isLoading: Boolean = true,
    val hasProfile: Boolean = false,
    val profile: PhotographerRegistration? = null,
    val error: String? = null
)

class PhotographerMainViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotographerMainUiState())
    val uiState: StateFlow<PhotographerMainUiState> = _uiState.asStateFlow()

    private var profileJob: Job? = null

    init {
        observeProfile()
    }

    private fun observeProfile() {
        // Cancel any existing job
        profileJob?.cancel()

        profileJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Continuously observe profile changes from Room database
                photographerRepository.getCurrentPhotographerProfile().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Only show loading on initial load
                            if (_uiState.value.profile == null) {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                        }

                        is Resource.Success -> {
                            val profile = resource.data
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    hasProfile = profile?.isProfileComplete == true,
                                    profile = profile,
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    hasProfile = false,
                                    error = resource.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasProfile = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun refreshProfile() {
        observeProfile()
    }

    fun updateAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.profile ?: return@launch

            // Optimistic update
            _uiState.update {
                it.copy(profile = currentProfile.copy(isAvailable = isAvailable))
            }

            // Persist the change
            val result = photographerRepository.updateAvailability(isAvailable)

            if (result is Resource.Error) {
                // Revert on error
                _uiState.update {
                    it.copy(
                        profile = currentProfile,
                        error = result.message
                    )
                }
            }
        }
    }
}
