package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetLibrarySectionsUseCase @Inject constructor(
    private val repository: PlexRepository
){
    suspend operator fun invoke(): NetworkResponse<List<LibrarySection>> = repository.getLibrarySections()
}
