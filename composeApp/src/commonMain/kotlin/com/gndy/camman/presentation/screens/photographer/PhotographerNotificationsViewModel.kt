package com.gndy.camman.presentation.screens.photographer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.data.repository.ClientBookingRepositoryImpl
import com.gndy.camman.domain.model.PhotographerNotification
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<PhotographerNotification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

class PhotographerNotificationsViewModel(
    private val bookingRepository: ClientBookingRepositoryImpl,
    private val photographerRepository: PhotographerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var photographerId: String? = null

    init {
        loadPhotographerAndNotifications()
    }

    private fun loadPhotographerAndNotifications() {
        viewModelScope.launch {
            // Get current photographer's ID
            val result = photographerRepository.getCurrentPhotographerProfile().first()
            photographerId = when (result) {
                is Resource.Success -> result.data?.id
                else -> null
            }

            if (photographerId != null) {
                loadNotifications()
                observeUnreadCount()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadNotifications() {
        val id = photographerId ?: return
        viewModelScope.launch {
            bookingRepository.getPhotographerNotifications(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notifications = result.data ?: emptyList(),
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun observeUnreadCount() {
        val id = photographerId ?: return
        viewModelScope.launch {
            bookingRepository.getUnreadNotificationCount(id).collect { count ->
                _uiState.value = _uiState.value.copy(unreadCount = count)
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            bookingRepository.markNotificationAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        val id = photographerId ?: return
        viewModelScope.launch {
            bookingRepository.markAllNotificationsAsRead(id)
        }
    }

    fun refresh() {
        loadPhotographerAndNotifications()
    }
}
