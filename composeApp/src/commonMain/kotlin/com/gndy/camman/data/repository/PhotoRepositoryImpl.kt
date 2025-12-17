package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.PhotoDao
import com.gndy.camman.data.local.entity.PhotoEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.domain.model.Photo
import com.gndy.camman.domain.repository.PhotoRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PhotoRepositoryImpl(
    private val photoDao: PhotoDao,
    private val apiService: CamManApiService
) : PhotoRepository {

    override fun getPhotosByAlbum(albumId: String): Flow<Resource<List<Photo>>> = flow {
        emit(Resource.Loading())

        photoDao.getPhotosByAlbum(albumId).collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPhoto() }))
            }
        }

        try {
            val response = apiService.getPhotosByAlbum(albumId)
            val photos = response.photos.map { it.toPhoto() }

            photoDao.deletePhotosByAlbum(albumId)
            photoDao.insertPhotos(photos.map { PhotoEntity.fromPhoto(it) })

            emit(Resource.Success(photos))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch photos"))
        }
    }

    override fun getPhotoById(photoId: String): Flow<Resource<Photo>> = flow {
        emit(Resource.Loading())

        photoDao.getPhotoById(photoId).collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toPhoto()))
            } else {
                try {
                    val photoDto = apiService.getPhotoById(photoId)
                    val photo = photoDto.toPhoto()
                    photoDao.insertPhoto(PhotoEntity.fromPhoto(photo))
                    emit(Resource.Success(photo))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Photo not found"))
                }
            }
        }
    }

    override fun getFeaturedPhotos(): Flow<Resource<List<Photo>>> = flow {
        emit(Resource.Loading())

        photoDao.getFeaturedPhotos().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPhoto() }))
            }
        }

        try {
            val response = apiService.getFeaturedPhotos()
            val photos = response.photos.map { it.toPhoto() }
            emit(Resource.Success(photos))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch featured photos"))
        }
    }

    override fun searchPhotos(query: String): Flow<Resource<List<Photo>>> = flow {
        emit(Resource.Loading())

        photoDao.searchPhotos(query).collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toPhoto() }))
            }
        }

        try {
            val response = apiService.searchPhotos(query)
            val photos = response.photos.map { it.toPhoto() }
            emit(Resource.Success(photos))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Search failed"))
        }
    }

    override suspend fun refreshPhotos(albumId: String): Resource<Unit> {
        return try {
            val response = apiService.getPhotosByAlbum(albumId)
            val photos = response.photos.map { it.toPhoto() }
            photoDao.deletePhotosByAlbum(albumId)
            photoDao.insertPhotos(photos.map { PhotoEntity.fromPhoto(it) })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh photos")
        }
    }
}
