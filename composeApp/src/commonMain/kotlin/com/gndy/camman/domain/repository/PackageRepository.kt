package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.PackageAddOn
import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PackageRepository {
    fun getPackages(): Flow<Resource<List<PhotographyPackage>>>
    fun getPackageById(packageId: String): Flow<Resource<PhotographyPackage>>
    fun getPackagesByCategory(category: PackageCategory): Flow<Resource<List<PhotographyPackage>>>
    fun getPopularPackages(): Flow<Resource<List<PhotographyPackage>>>
    fun getPackageAddOns(packageId: String): Flow<Resource<List<PackageAddOn>>>
    suspend fun refreshPackages(): Resource<Unit>
}
