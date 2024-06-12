package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetSongUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(songId: String): NetworkResponse<Song> = repository.getSong(songId)
}
