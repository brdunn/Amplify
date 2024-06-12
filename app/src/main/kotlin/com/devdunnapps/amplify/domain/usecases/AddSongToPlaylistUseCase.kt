package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class AddSongToPlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(songId: String, playlistId: String): Boolean =
        repository.addSongToPlaylist(songId, playlistId)
}
