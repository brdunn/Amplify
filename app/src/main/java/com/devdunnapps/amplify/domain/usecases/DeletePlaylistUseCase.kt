package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(playlistId: String): Flow<Resource<Playlist>> = repository.deletePlaylist(playlistId)
}
