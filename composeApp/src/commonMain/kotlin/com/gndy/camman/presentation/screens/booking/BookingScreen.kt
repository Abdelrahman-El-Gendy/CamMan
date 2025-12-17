package com.gndy.camman.presentation.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.presentation.components.DateTimePicker
import com.gndy.camman.presentation.components.formatDate
import com.gndy.camman.presentation.components.formatTime
import com.gndy.camman.presentation.theme.Gold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    packageId: String,
    photographerId: String? = null,
    onNavigateBack: () -> Unit,
    onBookingComplete: (String) -> Unit,
    viewModel: BookingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(packageId, photographerId) {
        viewModel.loadPackage(
            packageId = packageId,
            photographerId = photographerId ?: "mock_photographer",
            photographerName = "Photographer"
        )
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is BookingUiEvent.BookingComplete -> onBookingComplete(event.bookingId)
                is BookingUiEvent.ShowError -> { /* Show snackbar */
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Session") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress Indicator
            BookingProgressIndicator(currentStep = uiState.currentStep)

            if (uiState.isLoading && uiState.selectedPackage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                // Content based on current step
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (uiState.currentStep) {
                        BookingStep.PACKAGE_SELECTION -> PackageSelectionStep(uiState)
                        BookingStep.DATE_TIME -> DateTimeStep(uiState, viewModel)
                        BookingStep.CLIENT_DETAILS -> ClientDetailsStep(uiState, viewModel)
                        BookingStep.PAYMENT_METHOD -> PaymentMethodStep(uiState, viewModel)
                        BookingStep.CONFIRMATION -> ConfirmationStep(uiState, viewModel)
                    }
                }

                // Bottom Navigation
                BookingBottomBar(
                    currentStep = uiState.currentStep,
                    isLoading = uiState.isLoading,
                    onPrevious = viewModel::previousStep,
                    onNext = viewModel::nextStep,
                    onConfirm = viewModel::confirmBooking
                )
            }
        }
    }
}

@Composable
private fun BookingProgressIndicator(currentStep: BookingStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { currentStep.stepNumber / 5f },
            modifier = Modifier.fillMaxWidth(),
            color = Gold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Step ${currentStep.stepNumber} of 5",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun PackageSelectionStep(uiState: BookingUiState) {
    Column {
        Text(
            text = "Selected Package",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        uiState.selectedPackage?.let { pkg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = pkg.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pkg.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${pkg.price.toInt()} ${pkg.currency}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Duration: ${pkg.duration.formatted()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTimeStep(uiState: BookingUiState, viewModel: BookingViewModel) {
    Column {
        Text(
            text = "Choose Date & Time",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your preferred date and time for the session",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date & Time Picker
        DateTimePicker(
            selectedDate = uiState.selectedDate,
            selectedTime = uiState.selectedTime,
            onDateSelected = viewModel::onDateSelected,
            onTimeSelected = viewModel::onTimeSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Input
        OutlinedTextField(
            value = uiState.location,
            onValueChange = viewModel::onLocationChanged,
            label = { Text("Session Location *") },
            placeholder = { Text("Enter address or location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Gold
                )
            }
        )
    }
}

@Composable
private fun ClientDetailsStep(uiState: BookingUiState, viewModel: BookingViewModel) {
    Column {
        Text(
            text = "Your Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.clientName,
            onValueChange = viewModel::onClientNameChanged,
            label = { Text("Full Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.clientEmail,
            onValueChange = viewModel::onClientEmailChanged,
            label = { Text("Email Address *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.clientPhone,
            onValueChange = viewModel::onClientPhoneChanged,
            label = { Text("Phone Number *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.clientAddress,
            onValueChange = viewModel::onClientAddressChanged,
            label = { Text("Address (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.specialRequests,
            onValueChange = viewModel::onSpecialRequestsChanged,
            label = { Text("Special Requests (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
private fun PaymentMethodStep(uiState: BookingUiState, viewModel: BookingViewModel) {
    Column {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select how you'd like to pay",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        uiState.paymentMethods.forEach { method ->
            PaymentMethodCard(
                paymentMethod = method,
                isSelected = uiState.selectedPaymentMethod?.id == method.id,
                onClick = { viewModel.onPaymentMethodSelected(method) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.paymentMethods.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pay at session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Payment can be made on the day of your session",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Gold.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Gold)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = paymentMethod.type.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ConfirmationStep(uiState: BookingUiState, viewModel: BookingViewModel) {
    Column {
        Text(
            text = "Booking Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please review your booking details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Package
                SummaryRow("Package", uiState.selectedPackage?.name ?: "-")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Date & Time
                SummaryRow(
                    "Date",
                    uiState.selectedDate?.let { formatDate(it) } ?: "To be scheduled"
                )
                SummaryRow(
                    "Time",
                    uiState.selectedTime?.let { formatTime(it) } ?: "-"
                )
                SummaryRow("Location", uiState.location.ifBlank { "-" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Client
                SummaryRow("Name", uiState.clientName)
                SummaryRow("Email", uiState.clientEmail)
                SummaryRow("Phone", uiState.clientPhone)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Payment
                SummaryRow(
                    "Payment",
                    uiState.selectedPaymentMethod?.displayName ?: "Pay at session"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Deposit (30%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${uiState.depositAmount.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${uiState.totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.bookingNotes,
            onValueChange = viewModel::onNotesChanged,
            label = { Text("Additional Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            )
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BookingBottomBar(
    currentStep: BookingStep,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep != BookingStep.PACKAGE_SELECTION) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Previous")
                }
            }

            Button(
                onClick = if (currentStep == BookingStep.CONFIRMATION) onConfirm else onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.height(20.dp).width(20.dp)
                    )
                } else {
                    Text(
                        text = if (currentStep == BookingStep.CONFIRMATION) "Confirm Booking" else "Next",
                        color = Color.Black
                    )
                }
            }
        }
    }
}
