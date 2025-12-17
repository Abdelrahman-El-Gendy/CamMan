package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.PackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages WHERE isActive = 1 ORDER BY price ASC")
    fun getAllPackages(): Flow<List<PackageEntity>>

    @Query("SELECT * FROM packages WHERE id = :packageId")
    fun getPackageById(packageId: String): Flow<PackageEntity?>

    @Query("SELECT * FROM packages WHERE category = :category AND isActive = 1 ORDER BY price ASC")
    fun getPackagesByCategory(category: String): Flow<List<PackageEntity>>

    @Query("SELECT * FROM packages WHERE isPopular = 1 AND isActive = 1 ORDER BY price ASC")
    fun getPopularPackages(): Flow<List<PackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: PackageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<PackageEntity>)

    @Update
    suspend fun updatePackage(pkg: PackageEntity)

    @Delete
    suspend fun deletePackage(pkg: PackageEntity)

    @Query("DELETE FROM packages")
    suspend fun deleteAllPackages()
}
