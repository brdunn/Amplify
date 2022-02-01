package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(songId: String): Flow<Resource<Song>> = repository.getSong(songId)
}
