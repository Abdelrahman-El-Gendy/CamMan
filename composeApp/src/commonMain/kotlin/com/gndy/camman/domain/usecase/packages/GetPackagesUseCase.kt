package com.gndy.camman.domain.usecase.packages

import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.repository.PackageRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetPackagesUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(): Flow<Resource<List<PhotographyPackage>>> {
        return packageRepository.getPackages()
    }

    fun getByCategory(category: PackageCategory): Flow<Resource<List<PhotographyPackage>>> {
        return packageRepository.getPackagesByCategory(category)
    }

    fun getPackageById(packageId: String): Flow<Resource<PhotographyPackage>> {
        return packageRepository.getPackageById(packageId)
    }

    fun getPopularPackages(): Flow<Resource<List<PhotographyPackage>>> {
        return packageRepository.getPopularPackages()
    }
}
