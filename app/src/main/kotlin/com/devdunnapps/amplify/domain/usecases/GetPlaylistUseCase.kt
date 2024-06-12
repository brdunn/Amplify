package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(playlistId: String): NetworkResponse<Playlist> = repository.getPlaylist(playlistId)
}
