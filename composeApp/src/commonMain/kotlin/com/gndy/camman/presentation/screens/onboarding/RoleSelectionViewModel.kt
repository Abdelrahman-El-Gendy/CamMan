package com.gndy.camman.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.domain.repository.RoleValidationError
import com.gndy.camman.domain.repository.UserRoleRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Role Selection Screen
 */
data class RoleSelectionUiState(
    val selectedRole: UserType? = null,
    val isLoading: Boolean = false,
    val isValidating: Boolean = false,
    val isSuccess: Boolean = false,
    val error: RoleSelectionError? = null,
    val canContinue: Boolean = false
)

/**
 * Role selection errors
 */
sealed class RoleSelectionError {
    data object RoleAlreadyExists : RoleSelectionError()
    data object InvalidRole : RoleSelectionError()
    data object NetworkError : RoleSelectionError()
    data object ServerError : RoleSelectionError()
    data object UserNotAuthenticated : RoleSelectionError()
    data class Unknown(val message: String) : RoleSelectionError()
    
    fun toUserMessage(): String = when (this) {
        is RoleAlreadyExists -> "You have already selected a role. This cannot be changed."
        is InvalidRole -> "Invalid role selection. Please choose Client or Photographer."
        is NetworkError -> "Network error. Please check your connection and try again."
        is ServerError -> "Server error. Please try again later."
        is UserNotAuthenticated -> "Please sign in to continue."
        is Unknown -> message
    }
}

/**
 * One-time UI events
 */
sealed class RoleSelectionUiEvent {
    data class NavigateToHome(val role: UserType) : RoleSelectionUiEvent()
    data object NavigateToSignIn : RoleSelectionUiEvent()
}

/**
 * ViewModel for Role Selection Screen
 */
class RoleSelectionViewModel(
    private val authRepository: AuthRepository,
    private val userRoleRepository: UserRoleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoleSelectionUiState())
    val uiState: StateFlow<RoleSelectionUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<RoleSelectionUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        checkExistingRole()
    }

    /**
     * Check if user already has a role assigned
     */
    private fun checkExistingRole() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                _uiEvents.emit(RoleSelectionUiEvent.NavigateToSignIn)
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            when (val result = userRoleRepository.getUserRole(currentUser.uid)) {
                is Resource.Success -> {
                    val existingRole = result.data
                    if (existingRole != null) {
                        // User already has a role, navigate directly
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        _uiEvents.emit(RoleSelectionUiEvent.NavigateToHome(existingRole))
                    } else {
                        // No role yet, show selection screen
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                is Resource.Error -> {
                    // Profile might not exist yet, create it and show selection
                    createProfileAndContinue(currentUser.uid, currentUser.email, currentUser.displayName)
                }
                is Resource.Loading -> {
                    // Continue loading
                }
            }
        }
    }

    /**
     * Create user profile if it doesn't exist
     */
    private suspend fun createProfileAndContinue(userId: String, email: String?, displayName: String?) {
        when (val result = userRoleRepository.createProfileIfNotExists(userId, email, displayName)) {
            is Resource.Success -> {
                _uiState.update { it.copy(isLoading = false) }
            }
            is Resource.Error -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = RoleSelectionError.Unknown(result.message ?: "Failed to create profile")
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    /**
     * Handle role selection
     */
    fun onRoleSelected(role: UserType) {
        _uiState.update { 
            it.copy(
                selectedRole = role,
                canContinue = true,
                error = null
            )
        }
    }

    /**
     * Continue with selected role (validates and saves)
     */
    fun onContinue() {
        val selectedRole = _uiState.value.selectedRole ?: return
        val currentUser = authRepository.currentUser
        
        if (currentUser == null) {
            _uiState.update { it.copy(error = RoleSelectionError.UserNotAuthenticated) }
            viewModelScope.launch {
                _uiEvents.emit(RoleSelectionUiEvent.NavigateToSignIn)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isValidating = true, error = null) }

            // Step 1: Validate role assignment
            when (val validationResult = userRoleRepository.validateRoleAssignment(currentUser.uid, selectedRole)) {
                is Resource.Success -> {
                    if (validationResult.data == true) {
                        // Step 2: Set the role
                        setRole(currentUser.uid, selectedRole)
                    } else {
                        _uiState.update {
                            it.copy(
                                isValidating = false,
                                error = RoleSelectionError.InvalidRole
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    val error = parseValidationError(validationResult.message)
                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            error = error
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Set the user's role after validation
     */
    private suspend fun setRole(userId: String, role: UserType) {
        when (val result = userRoleRepository.setUserRole(userId, role)) {
            is Resource.Success -> {
                _uiState.update { 
                    it.copy(
                        isValidating = false,
                        isSuccess = true
                    )
                }
                // Small delay for success animation
                kotlinx.coroutines.delay(500)
                result.data?.let { userType ->
                    _uiEvents.emit(RoleSelectionUiEvent.NavigateToHome(userType))
                }
            }
            is Resource.Error -> {
                val error = parseValidationError(result.message)
                _uiState.update {
                    it.copy(
                        isValidating = false,
                        error = error
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    /**
     * Parse error messages to RoleSelectionError
     */
    private fun parseValidationError(message: String?): RoleSelectionError {
        return when {
            message?.contains(RoleValidationError.ROLE_ALREADY_EXISTS.name) == true -> 
                RoleSelectionError.RoleAlreadyExists
            message?.contains(RoleValidationError.INVALID_ROLE.name) == true -> 
                RoleSelectionError.InvalidRole
            message?.contains(RoleValidationError.NETWORK_ERROR.name) == true -> 
                RoleSelectionError.NetworkError
            message?.contains(RoleValidationError.SERVER_ERROR.name) == true -> 
                RoleSelectionError.ServerError
            message?.contains(RoleValidationError.USER_NOT_FOUND.name) == true -> 
                RoleSelectionError.UserNotAuthenticated
            message?.contains("network", ignoreCase = true) == true -> 
                RoleSelectionError.NetworkError
            message?.contains("already", ignoreCase = true) == true -> 
                RoleSelectionError.RoleAlreadyExists
            else -> RoleSelectionError.Unknown(message ?: "An unknown error occurred")
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retry after error
     */
    fun retry() {
        clearError()
        if (_uiState.value.selectedRole != null) {
            onContinue()
        } else {
            checkExistingRole()
        }
    }
}
