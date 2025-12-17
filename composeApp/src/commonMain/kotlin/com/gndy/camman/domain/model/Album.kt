package com.gndy.camman.domain.model

import kotlinx.datetime.LocalDate

data class Album(
    val id: String,
    val title: String,
    val description: String,
    val coverImageUrl: String,
    val category: AlbumCategory,
    val photoCount: Int,
    val createdAt: LocalDate,
    val isFeatured: Boolean = false
)

enum class AlbumCategory(val displayName: String) {
    WEDDING("Wedding"),
    PORTRAIT("Portrait"),
    EVENT("Event"),
    LANDSCAPE("Landscape"),
    PRODUCT("Product"),
    FASHION("Fashion"),
    LIFESTYLE("Lifestyle"),
    CORPORATE("Corporate"),
    FAMILY("Family"),
    MATERNITY("Maternity"),
    NEWBORN("Newborn"),
    OTHER("Other")
}
