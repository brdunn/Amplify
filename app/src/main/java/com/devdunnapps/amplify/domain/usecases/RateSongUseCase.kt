package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RateSongUseCase @Inject constructor(
    private val repository: PlexRepository
) {

    operator fun invoke(songId: String, rating: Int): Flow<Resource<Unit>> = repository.rateSong(songId, rating)
}
