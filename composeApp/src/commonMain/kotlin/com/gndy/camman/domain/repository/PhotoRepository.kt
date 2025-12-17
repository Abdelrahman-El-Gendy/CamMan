package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.Photo
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotosByAlbum(albumId: String): Flow<Resource<List<Photo>>>
    fun getPhotoById(photoId: String): Flow<Resource<Photo>>
    fun getFeaturedPhotos(): Flow<Resource<List<Photo>>>
    fun searchPhotos(query: String): Flow<Resource<List<Photo>>>
    suspend fun refreshPhotos(albumId: String): Resource<Unit>
}
