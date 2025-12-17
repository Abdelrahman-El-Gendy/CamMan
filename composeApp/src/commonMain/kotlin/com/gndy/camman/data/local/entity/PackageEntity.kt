package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PackageDuration
import com.gndy.camman.domain.model.PhotographyPackage

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val currency: String,
    val durationHours: Int,
    val durationMinutes: Int,
    val features: String,
    val deliverables: String,
    val maxPhotos: Int?,
    val includesEditing: Boolean,
    val includesPrints: Boolean,
    val turnaroundDays: Int,
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
        features = features.split("|").filter { it.isNotBlank() },
        deliverables = deliverables.split("|").filter { it.isNotBlank() },
        maxPhotos = maxPhotos,
        includesEditing = includesEditing,
        includesPrints = includesPrints,
        turnaroundDays = turnaroundDays,
        isPopular = isPopular,
        isActive = isActive
    )

    companion object {
        fun fromPackage(pkg: PhotographyPackage): PackageEntity = PackageEntity(
            id = pkg.id,
            name = pkg.name,
            description = pkg.description,
            category = pkg.category.name,
            price = pkg.price,
            currency = pkg.currency,
            durationHours = pkg.duration.hours,
            durationMinutes = pkg.duration.minutes,
            features = pkg.features.joinToString("|"),
            deliverables = pkg.deliverables.joinToString("|"),
            maxPhotos = pkg.maxPhotos,
            includesEditing = pkg.includesEditing,
            includesPrints = pkg.includesPrints,
            turnaroundDays = pkg.turnaroundDays,
            isPopular = pkg.isPopular,
            isActive = pkg.isActive
        )
    }
}
