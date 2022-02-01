package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLibrarySectionsUseCase @Inject constructor(
    private val repository: PlexTVRepository
){
    operator fun invoke(): Flow<Resource<List<LibrarySection>>> = repository.getLibrarySections()
}
