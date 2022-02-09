package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(artistId: String): Flow<Resource<Artist>> = repository.getArtist(artistId)
}
