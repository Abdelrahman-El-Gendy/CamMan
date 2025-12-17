package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.AlbumDao
import com.gndy.camman.data.local.entity.AlbumEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import com.gndy.camman.domain.repository.AlbumRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AlbumRepositoryImpl(
    private val albumDao: AlbumDao,
    private val apiService: CamManApiService
) : AlbumRepository {

    override fun getAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())

        // First emit cached data
        albumDao.getAllAlbums().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toAlbum() }))
            }
        }

        // Try to fetch from remote
        try {
            val response = apiService.getAlbums()
            val albums = response.albums.map { it.toAlbum() }

            // Cache the data
            albumDao.deleteAllAlbums()
            albumDao.insertAlbums(albums.map { AlbumEntity.fromAlbum(it) })

            emit(Resource.Success(albums))
        } catch (e: Exception) {
            // If remote fails, emit error but data might still be available from cache
            emit(Resource.Error(e.message ?: "Failed to fetch albums"))
        }
    }

    override fun getAlbumById(albumId: String): Flow<Resource<Album>> = flow {
        emit(Resource.Loading())

        albumDao.getAlbumById(albumId).collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toAlbum()))
            } else {
                try {
                    val albumDto = apiService.getAlbumById(albumId)
                    val album = albumDto.toAlbum()
                    albumDao.insertAlbum(AlbumEntity.fromAlbum(album))
                    emit(Resource.Success(album))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Album not found"))
                }
            }
        }
    }

    override fun getAlbumsByCategory(category: AlbumCategory): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())

        albumDao.getAlbumsByCategory(category.name).collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toAlbum() }))
            }
        }

        try {
            val response = apiService.getAlbumsByCategory(category.name)
            val albums = response.albums.map { it.toAlbum() }
            emit(Resource.Success(albums))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch albums by category"))
        }
    }

    override fun getFeaturedAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())

        albumDao.getFeaturedAlbums().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toAlbum() }))
            }
        }

        try {
            val response = apiService.getFeaturedAlbums()
            val albums = response.albums.map { it.toAlbum() }
            emit(Resource.Success(albums))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch featured albums"))
        }
    }

    override suspend fun refreshAlbums(): Resource<Unit> {
        return try {
            val response = apiService.getAlbums()
            val albums = response.albums.map { it.toAlbum() }
            albumDao.deleteAllAlbums()
            albumDao.insertAlbums(albums.map { AlbumEntity.fromAlbum(it) })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh albums")
        }
    }
}
