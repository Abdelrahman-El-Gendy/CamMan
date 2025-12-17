package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.PackageDao
import com.gndy.camman.data.local.entity.PackageEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.domain.model.PackageAddOn
import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.repository.PackageRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PackageRepositoryImpl(
    private val packageDao: PackageDao,
    private val apiService: CamManApiService
) : PackageRepository {

    override fun getPackages(): Flow<Resource<List<PhotographyPackage>>> = flow {
        emit(Resource.Loading())

        packageDao.getAllPackages().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPackage() }))
            }
        }

        try {
            val response = apiService.getPackages()
            val packages = response.packages.map { it.toPackage() }

            packageDao.deleteAllPackages()
            packageDao.insertPackages(packages.map { PackageEntity.fromPackage(it) })

            emit(Resource.Success(packages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch packages"))
        }
    }

    override fun getPackageById(packageId: String): Flow<Resource<PhotographyPackage>> = flow {
        emit(Resource.Loading())

        packageDao.getPackageById(packageId).collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toPackage()))
            } else {
                try {
                    val packageDto = apiService.getPackageById(packageId)
                    val pkg = packageDto.toPackage()
                    packageDao.insertPackage(PackageEntity.fromPackage(pkg))
                    emit(Resource.Success(pkg))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Package not found"))
                }
            }
        }
    }

    override fun getPackagesByCategory(category: PackageCategory): Flow<Resource<List<PhotographyPackage>>> =
        flow {
            emit(Resource.Loading())

            packageDao.getPackagesByCategory(category.name).collect { entities ->
                if (entities.isNotEmpty()) {
                    emit(Resource.Success(entities.map { it.toPackage() }))
                }
            }

            try {
                val response = apiService.getPackagesByCategory(category.name)
                val packages = response.packages.map { it.toPackage() }
                emit(Resource.Success(packages))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to fetch packages by category"))
            }
        }

    override fun getPopularPackages(): Flow<Resource<List<PhotographyPackage>>> = flow {
        emit(Resource.Loading())

        packageDao.getPopularPackages().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPackage() }))
            }
        }

        try {
            val response = apiService.getPopularPackages()
            val packages = response.packages.map { it.toPackage() }
            emit(Resource.Success(packages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch popular packages"))
        }
    }

    override fun getPackageAddOns(packageId: String): Flow<Resource<List<PackageAddOn>>> = flow {
        emit(Resource.Loading())

        try {
            val addOns = apiService.getPackageAddOns(packageId).map {
                PackageAddOn(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    currency = it.currency
                )
            }
            emit(Resource.Success(addOns))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch add-ons"))
        }
    }

    override suspend fun refreshPackages(): Resource<Unit> {
        return try {
            val response = apiService.getPackages()
            val packages = response.packages.map { it.toPackage() }
            packageDao.deleteAllPackages()
            packageDao.insertPackages(packages.map { PackageEntity.fromPackage(it) })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh packages")
        }
    }
}
