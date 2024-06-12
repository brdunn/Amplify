package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchLibraryUseCase @Inject constructor(
    private val repository: PlexRepository
) {

    operator fun invoke(query: String): Flow<Resource<SearchResults>> = repository.searchLibrary(query)
}
