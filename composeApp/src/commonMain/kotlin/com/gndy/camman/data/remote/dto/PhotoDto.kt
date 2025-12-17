package com.gndy.camman.data.remote.dto

import com.gndy.camman.domain.model.Photo
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String,
    val albumId: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String? = null,
    val description: String? = null,
    val width: Int,
    val height: Int,
    val takenAt: String? = null,
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val order: Int = 0
) {
    fun toPhoto(): Photo = Photo(
        id = id,
        albumId = albumId,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        title = title,
        description = description,
        width = width,
        height = height,
        takenAt = takenAt?.let { LocalDateTime.parse(it) },
        location = location,
        tags = tags,
        isFeatured = isFeatured,
        order = order
    )
}

@Serializable
data class PhotosResponse(
    val photos: List<PhotoDto>,
    val totalCount: Int
)
