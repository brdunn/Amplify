package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(): Flow<Resource<List<Song>>> = repository.getSongs()
}
