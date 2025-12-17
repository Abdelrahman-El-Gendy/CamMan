package com.gndy.camman.data.remote.dto

import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PackageDuration
import com.gndy.camman.domain.model.PhotographyPackage
import kotlinx.serialization.Serializable

@Serializable
data class PackageDto(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val currency: String = "USD",
    val durationHours: Int,
    val durationMinutes: Int = 0,
    val features: List<String>,
    val deliverables: List<String>,
    val maxPhotos: Int? = null,
    val includesEditing: Boolean = true,
    val includesPrints: Boolean = false,
    val turnaroundDays: Int = 14,
    val isPopular: Boolean = false,
    val isActive: Boolean = true
) {
    fun toPackage(): PhotographyPackage = PhotographyPackage(
        id = id,
        name = name,
        description = description,
        category = PackageCategory.entries.find { it.name == category } ?: PackageCategory.PORTRAIT,
        price = price,
        currency = currency,
        duration = PackageDuration(durationHours, durationMinutes),
        features = features,
        deliverables = deliverables,
        maxPhotos = maxPhotos,
        includesEditing = includesEditing,
        includesPrints = includesPrints,
        turnaroundDays = turnaroundDays,
        isPopular = isPopular,
        isActive = isActive
    )
}

@Serializable
data class PackagesResponse(
    val packages: List<PackageDto>,
    val totalCount: Int
)

@Serializable
data class PackageAddOnDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD"
)
