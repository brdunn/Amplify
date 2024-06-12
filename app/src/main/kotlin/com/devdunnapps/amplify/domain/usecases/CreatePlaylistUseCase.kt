package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(playlistTitle: String): NetworkResponse<Unit> = repository.createPlaylist(playlistTitle)
}
