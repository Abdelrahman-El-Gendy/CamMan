package com.gndy.camman.presentation.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotographerDetailUiState(
    val isLoading: Boolean = true,
    val photographer: Photographer? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)

class PhotographerDetailViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotographerDetailUiState())
    val uiState: StateFlow<PhotographerDetailUiState> = _uiState.asStateFlow()

    fun loadPhotographer(photographerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = photographerRepository.getPhotographerById(photographerId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            photographer = result.data,
                            error = null
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load photographer"
                        )
                    }
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        // TODO: Persist favorite status to local storage
    }
}
