package com.gndy.camman.domain.usecase.album

import com.gndy.camman.domain.model.Photo
import com.gndy.camman.domain.repository.PhotoRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAlbumPhotosUseCase(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(albumId: String): Flow<Resource<List<Photo>>> {
        return photoRepository.getPhotosByAlbum(albumId)
    }

    fun getPhotoById(photoId: String): Flow<Resource<Photo>> {
        return photoRepository.getPhotoById(photoId)
    }

    fun getFeaturedPhotos(): Flow<Resource<List<Photo>>> {
        return photoRepository.getFeaturedPhotos()
    }
}
