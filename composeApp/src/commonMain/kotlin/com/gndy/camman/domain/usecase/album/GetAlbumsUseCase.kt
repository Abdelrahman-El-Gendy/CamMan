package com.gndy.camman.domain.usecase.album

import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import com.gndy.camman.domain.repository.AlbumRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAlbumsUseCase(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<Resource<List<Album>>> {
        return albumRepository.getAlbums()
    }

    fun getByCategory(category: AlbumCategory): Flow<Resource<List<Album>>> {
        return albumRepository.getAlbumsByCategory(category)
    }

    fun getFeatured(): Flow<Resource<List<Album>>> {
        return albumRepository.getFeaturedAlbums()
    }
}
