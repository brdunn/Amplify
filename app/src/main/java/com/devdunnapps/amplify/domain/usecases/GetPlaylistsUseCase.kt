package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(): Flow<Resource<List<Playlist>>> = repository.getPlaylists()
}
