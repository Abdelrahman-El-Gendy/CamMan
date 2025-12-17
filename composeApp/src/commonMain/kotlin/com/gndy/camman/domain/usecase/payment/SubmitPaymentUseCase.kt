package com.gndy.camman.domain.usecase.payment

import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentResult
import com.gndy.camman.domain.repository.PaymentRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class SubmitPaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        bookingId: String,
        paymentMethod: PaymentMethod,
        amount: Double,
        isDeposit: Boolean
    ): Resource<PaymentResult> {
        return paymentRepository.submitPayment(
            bookingId = bookingId,
            paymentMethod = paymentMethod,
            amount = amount,
            isDeposit = isDeposit
        )
    }

    fun getPaymentMethods(): Flow<Resource<List<PaymentMethod>>> {
        return paymentRepository.getPaymentMethods()
    }

    suspend fun addPaymentMethod(paymentMethod: PaymentMethod): Resource<PaymentMethod> {
        return paymentRepository.addPaymentMethod(paymentMethod)
    }

    suspend fun removePaymentMethod(paymentMethodId: String): Resource<Unit> {
        return paymentRepository.removePaymentMethod(paymentMethodId)
    }

    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Resource<Unit> {
        return paymentRepository.setDefaultPaymentMethod(paymentMethodId)
    }
}
