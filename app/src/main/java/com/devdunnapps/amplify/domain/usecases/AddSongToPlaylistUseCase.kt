package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddSongToPlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(songId: String, playlistId: String): Flow<Resource<Playlist>> = repository.addSongToPlaylist(songId, playlistId)
}
