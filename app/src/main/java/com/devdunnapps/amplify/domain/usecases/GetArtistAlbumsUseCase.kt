package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistAlbumsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(artistId: String): Flow<Resource<List<Album>>> = repository.getArtistAlbums(artistId)
}
