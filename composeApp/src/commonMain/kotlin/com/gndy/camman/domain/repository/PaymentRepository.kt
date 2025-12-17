package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentResult
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun submitPayment(
        bookingId: String,
        paymentMethod: PaymentMethod,
        amount: Double,
        isDeposit: Boolean
    ): Resource<PaymentResult>

    fun getPaymentMethods(): Flow<Resource<List<PaymentMethod>>>
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod): Resource<PaymentMethod>
    suspend fun removePaymentMethod(paymentMethodId: String): Resource<Unit>
    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Resource<Unit>
    fun getPaymentHistory(bookingId: String): Flow<Resource<List<PaymentResult>>>
}
