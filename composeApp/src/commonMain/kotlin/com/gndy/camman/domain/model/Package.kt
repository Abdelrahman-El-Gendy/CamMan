package com.gndy.camman.domain.model

data class PhotographyPackage(
    val id: String,
    val name: String,
    val description: String,
    val category: PackageCategory,
    val price: Double,
    val currency: String = "USD",
    val duration: PackageDuration,
    val features: List<String>,
    val deliverables: List<String>,
    val maxPhotos: Int?,
    val includesEditing: Boolean,
    val includesPrints: Boolean,
    val turnaroundDays: Int,
    val isPopular: Boolean = false,
    val isActive: Boolean = true
)

enum class PackageCategory(val displayName: String) {
    WEDDING("Wedding Photography"),
    PORTRAIT("Portrait Session"),
    EVENT("Event Coverage"),
    PRODUCT("Product Photography"),
    CORPORATE("Corporate/Headshots"),
    FAMILY("Family Session"),
    MATERNITY("Maternity Shoot"),
    NEWBORN("Newborn Photography"),
    ENGAGEMENT("Engagement Session"),
    MINI_SESSION("Mini Session")
}

data class PackageDuration(
    val hours: Int,
    val minutes: Int = 0
) {
    val totalMinutes: Int get() = hours * 60 + minutes

    fun formatted(): String = when {
        minutes == 0 -> "$hours hour${if (hours > 1) "s" else ""}"
        hours == 0 -> "$minutes minutes"
        else -> "$hours hour${if (hours > 1) "s" else ""} $minutes min"
    }
}

data class PackageAddOn(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD"
)
