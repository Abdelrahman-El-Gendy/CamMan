package com.gndy.camman.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubmitPaymentRequest(
    val bookingId: String,
    val paymentMethodId: String,
    val amount: Double,
    val isDeposit: Boolean
)

@Serializable
data class PaymentResultDto(
    val success: Boolean,
    val transactionId: String?,
    val errorMessage: String?,
    val timestamp: Long
)

@Serializable
data class PaymentMethodDto(
    val id: String,
    val type: String,
    val displayName: String,
    val isDefault: Boolean = false
)

@Serializable
data class AddPaymentMethodRequest(
    val type: String,
    val displayName: String,
    val cardNumber: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val cvv: String? = null,
    val cardholderName: String? = null,
    val email: String? = null
)
