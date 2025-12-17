package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import kotlinx.datetime.LocalDate

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey
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

    companion object {
        fun fromAlbum(album: Album): AlbumEntity = AlbumEntity(
            id = album.id,
            title = album.title,
            description = album.description,
            coverImageUrl = album.coverImageUrl,
            category = album.category.name,
            photoCount = album.photoCount,
            createdAt = album.createdAt.toString(),
            isFeatured = album.isFeatured
        )
    }
}
