package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class RemoveSongFromPlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(songId: String, playlistId: String): Boolean = repository.removeSongFromPlaylist(songId, playlistId)
}
