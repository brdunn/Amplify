package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(playlistId: String): NetworkResponse<Unit> = repository.deletePlaylist(playlistId)
}
