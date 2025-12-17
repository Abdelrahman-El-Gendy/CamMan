package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<Resource<List<Album>>>
    fun getAlbumById(albumId: String): Flow<Resource<Album>>
    fun getAlbumsByCategory(category: AlbumCategory): Flow<Resource<List<Album>>>
    fun getFeaturedAlbums(): Flow<Resource<List<Album>>>
    suspend fun refreshAlbums(): Resource<Unit>
}
