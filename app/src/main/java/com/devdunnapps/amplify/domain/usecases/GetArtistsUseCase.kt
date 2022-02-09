package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: PlexRepository
){
    operator fun invoke(): Flow<Resource<List<Artist>>> = repository.getArtists()
}
