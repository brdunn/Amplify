package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetArtistAlbumsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(artistId: String): NetworkResponse<List<Album>> = repository.getArtistAlbums(artistId)
}
