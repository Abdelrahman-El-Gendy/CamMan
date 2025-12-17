package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentType

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val displayName: String,
    val detailsJson: String?,
    val isDefault: Boolean = false
) {
    fun toPaymentMethod(): PaymentMethod = PaymentMethod(
        id = id,
        type = PaymentType.entries.find { it.name == type } ?: PaymentType.CREDIT_CARD,
        displayName = displayName,
        details = null,
        isDefault = isDefault
    )

    companion object {
        fun fromPaymentMethod(paymentMethod: PaymentMethod): PaymentMethodEntity =
            PaymentMethodEntity(
                id = paymentMethod.id,
                type = paymentMethod.type.name,
                displayName = paymentMethod.displayName,
                detailsJson = null,
                isDefault = paymentMethod.isDefault
            )
    }
}
