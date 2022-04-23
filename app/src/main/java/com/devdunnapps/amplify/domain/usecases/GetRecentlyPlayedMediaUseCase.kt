package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.MixedMedia
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentlyPlayedMediaUseCase @Inject constructor(
    private val repository: PlexRepository
) {

    operator fun invoke(): Flow<Resource<MixedMedia>> = repository.getRecentlyPlayedMedia()
}
