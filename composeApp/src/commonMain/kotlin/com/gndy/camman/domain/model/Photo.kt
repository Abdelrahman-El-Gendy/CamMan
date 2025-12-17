package com.gndy.camman.domain.model

import kotlinx.datetime.LocalDateTime

data class Photo(
    val id: String,
    val albumId: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String?,
    val description: String?,
    val width: Int,
    val height: Int,
    val takenAt: LocalDateTime?,
    val location: String?,
    val tags: List<String>,
    val isFeatured: Boolean = false,
    val order: Int = 0
)

data class PhotoMetadata(
    val camera: String?,
    val lens: String?,
    val aperture: String?,
    val shutterSpeed: String?,
    val iso: Int?,
    val focalLength: String?
)
