package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.PaymentMethodDao
import com.gndy.camman.data.local.entity.PaymentMethodEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.data.remote.dto.AddPaymentMethodRequest
import com.gndy.camman.data.remote.dto.SubmitPaymentRequest
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentResult
import com.gndy.camman.domain.model.PaymentType
import com.gndy.camman.domain.repository.PaymentRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentRepositoryImpl(
    private val paymentMethodDao: PaymentMethodDao,
    private val apiService: CamManApiService
) : PaymentRepository {

    override suspend fun submitPayment(
        bookingId: String,
        paymentMethod: PaymentMethod,
        amount: Double,
        isDeposit: Boolean
    ): Resource<PaymentResult> {
        return try {
            val request = SubmitPaymentRequest(
                bookingId = bookingId,
                paymentMethodId = paymentMethod.id,
                amount = amount,
                isDeposit = isDeposit
            )

            val response = apiService.submitPayment(request)

            Resource.Success(
                PaymentResult(
                    success = response.success,
                    transactionId = response.transactionId,
                    errorMessage = response.errorMessage,
                    timestamp = response.timestamp
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Payment failed")
        }
    }

    override fun getPaymentMethods(): Flow<Resource<List<PaymentMethod>>> = flow {
        emit(Resource.Loading())

        paymentMethodDao.getAllPaymentMethods().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPaymentMethod() }))
            }
        }

        try {
            val response = apiService.getPaymentMethods()
            val paymentMethods = response.map {
                PaymentMethod(
                    id = it.id,
                    type = PaymentType.entries.find { type -> type.name == it.type }
                        ?: PaymentType.CREDIT_CARD,
                    displayName = it.displayName,
                    details = null,
                    isDefault = it.isDefault
                )
            }

            // Update local cache
            paymentMethods.forEach { method ->
                paymentMethodDao.insertPaymentMethod(PaymentMethodEntity.fromPaymentMethod(method))
            }

            emit(Resource.Success(paymentMethods))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch payment methods"))
        }
    }

    override suspend fun addPaymentMethod(paymentMethod: PaymentMethod): Resource<PaymentMethod> {
        return try {
            val request = AddPaymentMethodRequest(
                type = paymentMethod.type.name,
                displayName = paymentMethod.displayName
            )

            val response = apiService.addPaymentMethod(request)

            val newMethod = PaymentMethod(
                id = response.id,
                type = PaymentType.entries.find { it.name == response.type }
                    ?: PaymentType.CREDIT_CARD,
                displayName = response.displayName,
                details = null,
                isDefault = response.isDefault
            )

            paymentMethodDao.insertPaymentMethod(PaymentMethodEntity.fromPaymentMethod(newMethod))

            Resource.Success(newMethod)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add payment method")
        }
    }

    override suspend fun removePaymentMethod(paymentMethodId: String): Resource<Unit> {
        return try {
            apiService.removePaymentMethod(paymentMethodId)
            paymentMethodDao.deletePaymentMethodById(paymentMethodId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove payment method")
        }
    }

    override suspend fun setDefaultPaymentMethod(paymentMethodId: String): Resource<Unit> {
        return try {
            apiService.setDefaultPaymentMethod(paymentMethodId)
            paymentMethodDao.clearDefaultPaymentMethods()
            paymentMethodDao.setDefaultPaymentMethod(paymentMethodId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to set default payment method")
        }
    }

    override fun getPaymentHistory(bookingId: String): Flow<Resource<List<PaymentResult>>> = flow {
        emit(Resource.Loading())
        // Placeholder - would fetch from API
        emit(Resource.Success(emptyList()))
    }
}
