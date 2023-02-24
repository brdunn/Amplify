package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class RateSongUseCase @Inject constructor(
    private val repository: PlexRepository
) {

    suspend operator fun invoke(songId: String, rating: Int): NetworkResponse<Unit> = repository.rateSong(songId, rating)
}
