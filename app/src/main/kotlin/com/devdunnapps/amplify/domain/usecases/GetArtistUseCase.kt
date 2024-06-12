package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetArtistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(artistId: String): NetworkResponse<Artist> = repository.getArtist(artistId)
}
