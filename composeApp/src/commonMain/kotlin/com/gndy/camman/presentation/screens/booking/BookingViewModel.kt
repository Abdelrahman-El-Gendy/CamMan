package com.gndy.camman.presentation.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.data.repository.ClientBookingRepositoryImpl
import com.gndy.camman.domain.model.AvailableDate
import com.gndy.camman.domain.model.BookingAddOn
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PackageDuration
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentType
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.usecase.booking.CreateBookingUseCase
import com.gndy.camman.domain.usecase.packages.GetPackagesUseCase
import com.gndy.camman.domain.usecase.payment.SubmitPaymentUseCase
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

enum class BookingStep(val stepNumber: Int) {
    PACKAGE_SELECTION(1),
    DATE_TIME(2),
    CLIENT_DETAILS(3),
    PAYMENT_METHOD(4),
    CONFIRMATION(5)
}

data class BookingUiState(
    val currentStep: BookingStep = BookingStep.PACKAGE_SELECTION,
    val isLoading: Boolean = true,

    // Step 1 - Package
    val selectedPackage: PhotographyPackage? = null,
    val selectedAddOns: List<BookingAddOn> = emptyList(),

    // Step 2 - Date & Time
    val availableDates: List<AvailableDate> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val location: String = "",

    // Step 3 - Client Details
    val clientName: String = "",
    val clientEmail: String = "",
    val clientPhone: String = "",
    val clientAddress: String = "",
    val specialRequests: String = "",

    // Step 4 - Payment
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,

    // Step 5 - Summary
    val bookingNotes: String = "",
    val totalAmount: Double = 0.0,
    val depositAmount: Double = 0.0,

    val error: String? = null
)

sealed class BookingUiEvent {
    data class ShowError(val message: String) : BookingUiEvent()
    data class BookingComplete(val bookingId: String) : BookingUiEvent()
}

class BookingViewModel(
    private val getPackagesUseCase: GetPackagesUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val submitPaymentUseCase: SubmitPaymentUseCase,
    private val clientBookingRepository: ClientBookingRepositoryImpl
) : ViewModel() {

    // Store photographer info for notifications
    private var photographerId: String = ""
    private var photographerName: String = ""

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<BookingUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun loadPackage(packageId: String,
                    photographerId: String = "mock_photographer",
                    photographerName: String = "Photographer"
    ) {
        this.photographerId = photographerId
        this.photographerName = photographerName

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ===== MOCK DATA - Comment this block when backend is ready =====
            delay(500) // Simulate brief loading
            val pkg = getMockPackage(packageId)
            val paymentMethods = getMockPaymentMethods()

            _uiState.update {
                it.copy(
                    selectedPackage = pkg,
                    totalAmount = pkg.price,
                    depositAmount = pkg.price * 0.3,
                    paymentMethods = paymentMethods,
                    isLoading = false
                )
            }
            // ===== END MOCK DATA =====

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                getPackagesUseCase.getPackageById(packageId).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            val pkg = result.data
                            if (pkg != null) {
                                _uiState.update {
                                    it.copy(
                                        selectedPackage = pkg,
                                        totalAmount = pkg.price,
                                        depositAmount = pkg.price * 0.3,
                                        isLoading = false,
                                        error = null
                                    )
                                }
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
                
                // Load payment methods from repository
                paymentRepository.getPaymentMethods().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(paymentMethods = result.data ?: emptyList())
                            }
                        }
                        else -> {}
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

    private fun getMockPackage(packageId: String): PhotographyPackage {
        // Return package based on ID for accurate mock data
        val packages = mapOf(
            "1" to PhotographyPackage(
                id = "1",
                name = "Essential Portrait",
                description = "Perfect for headshots and personal branding.",
                category = PackageCategory.PORTRAIT,
                price = 199.0,
                currency = "USD",
                duration = PackageDuration(1, 0),
                features = listOf(
                    "1 hour session",
                    "1 location",
                    "15 edited photos",
                    "Online gallery"
                ),
                deliverables = listOf(
                    "High-resolution files",
                    "Online gallery",
                    "Print release"
                ),
                maxPhotos = 15,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 7,
                isPopular = false,
                isActive = true
            ),
            "2" to PhotographyPackage(
                id = "2",
                name = "Premium Portrait",
                description = "Our most popular portrait package",
                category = PackageCategory.PORTRAIT,
                price = 399.0,
                currency = "USD",
                duration = PackageDuration(2, 0),
                features = listOf(
                    "2 hour session",
                    "2 locations",
                    "30 edited photos",
                    "Online gallery"
                ),
                deliverables = listOf(
                    "High-resolution files",
                    "Online gallery",
                    "Print release"
                ),
                maxPhotos = 30,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 10,
                isPopular = true,
                isActive = true
            ),
            "3" to PhotographyPackage(
                id = "3",
                name = "Wedding Essentials",
                description = "Essential wedding day coverage",
                category = PackageCategory.WEDDING,
                price = 1999.0,
                currency = "USD",
                duration = PackageDuration(6, 0),
                features = listOf(
                    "6 hours coverage",
                    "1 photographer",
                    "200+ edited photos"
                ),
                deliverables = listOf(
                    "High-resolution files",
                    "USB drive",
                    "Online gallery"
                ),
                maxPhotos = 200,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 30,
                isPopular = false,
                isActive = true
            ),
            "4" to PhotographyPackage(
                id = "4",
                name = "Wedding Premium",
                description = "Complete wedding coverage",
                category = PackageCategory.WEDDING,
                price = 3499.0,
                currency = "USD",
                duration = PackageDuration(10, 0),
                features = listOf(
                    "10 hours coverage",
                    "2 photographers",
                    "400+ edited photos"
                ),
                deliverables = listOf(
                    "High-resolution files",
                    "Premium album",
                    "USB drive"
                ),
                maxPhotos = 400,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 45,
                isPopular = true,
                isActive = true
            ),
            "5" to PhotographyPackage(
                id = "5",
                name = "Event Coverage",
                description = "Professional event coverage",
                category = PackageCategory.EVENT,
                price = 599.0,
                currency = "USD",
                duration = PackageDuration(4, 0),
                features = listOf(
                    "4 hours coverage",
                    "100+ edited photos"
                ),
                deliverables = listOf(
                    "Digital files",
                    "Online gallery"
                ),
                maxPhotos = 100,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 5,
                isPopular = false,
                isActive = true
            ),
            "6" to PhotographyPackage(
                id = "6",
                name = "Mini Session",
                description = "Quick affordable session",
                category = PackageCategory.MINI_SESSION,
                price = 99.0,
                currency = "USD",
                duration = PackageDuration(0, 30),
                features = listOf(
                    "30 minute session",
                    "10 edited photos"
                ),
                deliverables = listOf(
                    "Digital files",
                    "Online gallery"
                ),
                maxPhotos = 10,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 3,
                isPopular = false,
                isActive = true
            )
        )

        return packages[packageId] ?: packages["2"]!!
    }

    private fun getMockPaymentMethods(): List<PaymentMethod> {
        return listOf(
            PaymentMethod(
                id = "1",
                type = PaymentType.CREDIT_CARD,
                displayName = "Credit Card",
                details = null,
                isDefault = true
            ),
            PaymentMethod(
                id = "2",
                type = PaymentType.PAYPAL,
                displayName = "PayPal",
                details = null,
                isDefault = false
            ),
            PaymentMethod(
                id = "3",
                type = PaymentType.CASH,
                displayName = "Cash (Pay at session)",
                details = null,
                isDefault = false
            )
        )
    }

    // Step Navigation
    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        val nextStep = BookingStep.entries.find { it.stepNumber == currentStep.stepNumber + 1 }
        if (nextStep != null && validateCurrentStep()) {
            _uiState.update { it.copy(currentStep = nextStep) }
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val prevStep = BookingStep.entries.find { it.stepNumber == currentStep.stepNumber - 1 }
        if (prevStep != null) {
            _uiState.update { it.copy(currentStep = prevStep) }
        }
    }

    private fun validateCurrentStep(): Boolean {
        val state = _uiState.value
        return when (state.currentStep) {
            BookingStep.PACKAGE_SELECTION -> state.selectedPackage != null
            BookingStep.DATE_TIME -> state.location.isNotBlank()
            BookingStep.CLIENT_DETAILS -> state.clientName.isNotBlank() &&
                    state.clientEmail.isNotBlank() && state.clientPhone.isNotBlank()

            BookingStep.PAYMENT_METHOD -> true
            BookingStep.CONFIRMATION -> true
        }
    }

    // Step 2 Updates
    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, selectedTime = null) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun onLocationChanged(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    // Step 3 Updates
    fun onClientNameChanged(name: String) {
        _uiState.update { it.copy(clientName = name) }
    }

    fun onClientEmailChanged(email: String) {
        _uiState.update { it.copy(clientEmail = email) }
    }

    fun onClientPhoneChanged(phone: String) {
        _uiState.update { it.copy(clientPhone = phone) }
    }

    fun onClientAddressChanged(address: String) {
        _uiState.update { it.copy(clientAddress = address) }
    }

    fun onSpecialRequestsChanged(requests: String) {
        _uiState.update { it.copy(specialRequests = requests) }
    }

    // Step 4 Updates
    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    // Step 5 - Submit
    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(bookingNotes = notes) }
    }

    fun confirmBooking() {
        val state = _uiState.value
        val pkg = state.selectedPackage ?: return

        // Use current date/time if not selected
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val sessionDate = state.selectedDate ?: LocalDate(now.year, now.monthNumber, now.dayOfMonth)
        val sessionTime = state.selectedTime ?: LocalTime(10, 0)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Create client info
                val clientInfo = ClientInfo(
                    fullName = state.clientName,
                    email = state.clientEmail,
                    phone = state.clientPhone,
                    address = state.clientAddress.ifBlank { null },
                    specialRequests = state.specialRequests.ifBlank { null }
                )

                // Create booking and notification via repository
                val result = clientBookingRepository.createBooking(
                    photographerId = photographerId,
                    photographerName = photographerName,
                    packageId = pkg.id,
                    packageName = pkg.name,
                    sessionDate = sessionDate,
                    sessionTime = sessionTime,
                    location = state.location,
                    clientInfo = clientInfo,
                    totalAmount = state.totalAmount,
                    paymentMethod = state.selectedPaymentMethod,
                    notes = state.bookingNotes.ifBlank { null }
                )

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _uiEvents.emit(BookingUiEvent.BookingComplete(result.data?.id ?: ""))
                    }

                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _uiEvents.emit(BookingUiEvent.ShowError(result.message ?: "Booking failed"))
                    }

                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvents.emit(BookingUiEvent.ShowError(e.message ?: "Booking failed"))
            }

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                val clientInfo = ClientInfo(
                    name = state.clientName,
                    email = state.clientEmail,
                    phone = state.clientPhone,
                    address = state.clientAddress
                )
                
                val result = createBookingUseCase(
                    packageId = packageId,
                    date = sessionDate,
                    time = sessionTime,
                    clientInfo = clientInfo,
                    location = state.location,
                    specialRequests = state.specialRequests,
                    addOnIds = state.selectedAddOns.map { it.id }
                ).first { it !is Resource.Loading }
                
                when (result) {
                    is Resource.Success -> {
                        val booking = result.data
                        if (booking != null) {
                            // Process payment if payment method selected
                            state.selectedPaymentMethod?.let { paymentMethod ->
                                submitPaymentUseCase(
                                    bookingId = booking.id,
                                    paymentMethodId = paymentMethod.id,
                                    amount = state.depositAmount
                                ).first { it !is Resource.Loading }
                            }
                            
                            _uiState.update { it.copy(isLoading = false) }
                            _uiEvents.emit(BookingUiEvent.BookingComplete(booking.id))
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _uiEvents.emit(BookingUiEvent.ShowError(result.message ?: "Booking failed"))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvents.emit(BookingUiEvent.ShowError(e.message ?: "Booking failed"))
            }
            // ===== END REAL IMPLEMENTATION =====
            */
        }
    }
}
