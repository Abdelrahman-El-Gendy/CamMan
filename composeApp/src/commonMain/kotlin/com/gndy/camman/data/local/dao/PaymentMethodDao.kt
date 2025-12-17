package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    @Query("SELECT * FROM payment_methods ORDER BY isDefault DESC")
    fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>

    @Query("SELECT * FROM payment_methods WHERE id = :paymentMethodId")
    fun getPaymentMethodById(paymentMethodId: String): Flow<PaymentMethodEntity?>

    @Query("SELECT * FROM payment_methods WHERE isDefault = 1 LIMIT 1")
    fun getDefaultPaymentMethod(): Flow<PaymentMethodEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethodEntity)

    @Update
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity)

    @Delete
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethodEntity)

    @Query("DELETE FROM payment_methods WHERE id = :paymentMethodId")
    suspend fun deletePaymentMethodById(paymentMethodId: String)

    @Query("UPDATE payment_methods SET isDefault = 0")
    suspend fun clearDefaultPaymentMethods()

    @Query("UPDATE payment_methods SET isDefault = 1 WHERE id = :paymentMethodId")
    suspend fun setDefaultPaymentMethod(paymentMethodId: String)
}
