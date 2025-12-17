package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.Photo
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["albumId"])]
)
data class PhotoEntity(
    @PrimaryKey
    val id: String,
    val albumId: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String?,
    val description: String?,
    val width: Int,
    val height: Int,
    val takenAt: String?,
    val location: String?,
    val tags: String,
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
        tags = tags.split(",").filter { it.isNotBlank() },
        isFeatured = isFeatured,
        order = order
    )

    companion object {
        fun fromPhoto(photo: Photo): PhotoEntity = PhotoEntity(
            id = photo.id,
            albumId = photo.albumId,
            imageUrl = photo.imageUrl,
            thumbnailUrl = photo.thumbnailUrl,
            title = photo.title,
            description = photo.description,
            width = photo.width,
            height = photo.height,
            takenAt = photo.takenAt?.toString(),
            location = photo.location,
            tags = photo.tags.joinToString(","),
            isFeatured = photo.isFeatured,
            order = photo.order
        )
    }
}
