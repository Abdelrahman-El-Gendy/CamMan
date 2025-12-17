package com.gndy.camman.presentation.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowsePhotographersUiState(
    val isLoading: Boolean = true,
    val photographers: List<Photographer> = emptyList(),
    val searchQuery: String = "",
    val selectedSpecialty: String? = null,
    val specialties: List<String> = listOf(
        "All",
        "Wedding",
        "Portrait",
        "Event",
        "Fashion",
        "Product",
        "Landscape",
        "Sports",
        "Family",
        "Newborn"
    ),
    val error: String? = null
)

sealed class BrowsePhotographersUiEvent {
    data class NavigateToPhotographer(val photographerId: String) : BrowsePhotographersUiEvent()
    data class ShowError(val message: String) : BrowsePhotographersUiEvent()
}

class BrowsePhotographersViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowsePhotographersUiState())
    val uiState: StateFlow<BrowsePhotographersUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<BrowsePhotographersUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var allPhotographers: List<Photographer> = emptyList()
    private var searchJob: Job? = null

    init {
        loadPhotographers()
    }

    private fun loadPhotographers() {
        viewModelScope.launch {
            photographerRepository.getAllPhotographers().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        allPhotographers = resource.data ?: emptyList()
                        filterPhotographers()
                        _uiState.update { it.copy(isLoading = false, error = null) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                        _uiEvents.emit(
                            BrowsePhotographersUiEvent.ShowError(
                                resource.message ?: "Failed to load photographers"
                            )
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // 300ms debounce
            filterPhotographers()
        }
    }

    fun onSpecialtySelected(specialty: String?) {
        val selected = if (specialty == "All") null else specialty
        _uiState.update { it.copy(selectedSpecialty = selected) }
        filterPhotographers()
    }

    private fun filterPhotographers() {
        val state = _uiState.value
        val filtered = allPhotographers.filter { photographer ->
            val matchesSearch = state.searchQuery.isBlank() ||
                    photographer.name.contains(state.searchQuery, ignoreCase = true) ||
                    photographer.location?.contains(state.searchQuery, ignoreCase = true) == true ||
                    photographer.specialties.any {
                        it.contains(state.searchQuery, ignoreCase = true)
                    }

            val matchesSpecialty = state.selectedSpecialty == null ||
                    photographer.specialties.any {
                        it.equals(state.selectedSpecialty, ignoreCase = true)
                    }

            matchesSearch && matchesSpecialty
        }
        _uiState.update { it.copy(photographers = filtered) }
    }

    fun onPhotographerClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(BrowsePhotographersUiEvent.NavigateToPhotographer(photographerId))
        }
    }

    fun refresh() {
        loadPhotographers()
    }
}
