package com.gndy.camman.data.remote.dto

import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val title: String,
    val description: String,
    val coverImageUrl: String,
    val category: String,
    val photoCount: Int,
    val createdAt: String,
    val isFeatured: Boolean = false
) {
    fun toAlbum(): Album = Album(
        id = id,
        title = title,
        description = description,
        coverImageUrl = coverImageUrl,
        category = AlbumCategory.entries.find { it.name == category } ?: AlbumCategory.OTHER,
        photoCount = photoCount,
        createdAt = LocalDate.parse(createdAt),
        isFeatured = isFeatured
    )
}

@Serializable
data class AlbumsResponse(
    val albums: List<AlbumDto>,
    val totalCount: Int
)
