package com.gndy.camman.presentation.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.data.repository.ClientBookingRepositoryImpl
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingConfirmationUiState(
    val isLoading: Boolean = true,
    val booking: Booking? = null,
    val error: String? = null
)

class BookingConfirmationViewModel(
    private val bookingRepository: ClientBookingRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingConfirmationUiState())
    val uiState: StateFlow<BookingConfirmationUiState> = _uiState.asStateFlow()

    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            bookingRepository.getBookingById(bookingId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                booking = result.data,
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
        }
    }
}
