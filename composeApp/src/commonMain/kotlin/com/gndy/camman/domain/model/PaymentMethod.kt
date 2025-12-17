package com.gndy.camman.domain.model

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val displayName: String,
    val details: PaymentDetails?,
    val isDefault: Boolean = false
)

enum class PaymentType(val displayName: String) {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank Transfer"),
    CASH("Cash"),
    VENMO("Venmo"),
    ZELLE("Zelle"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay")
}

sealed class PaymentDetails {
    data class Card(
        val lastFourDigits: String,
        val brand: CardBrand,
        val expiryMonth: Int,
        val expiryYear: Int,
        val cardholderName: String
    ) : PaymentDetails()

    data class BankAccount(
        val bankName: String,
        val accountLastFour: String,
        val accountType: String
    ) : PaymentDetails()

    data class DigitalWallet(
        val email: String
    ) : PaymentDetails()
}

enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    UNKNOWN("Card")
}

data class PaymentResult(
    val success: Boolean,
    val transactionId: String?,
    val errorMessage: String?,
    val timestamp: Long
)
