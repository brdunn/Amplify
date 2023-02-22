package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import javax.inject.Inject

class GetUsersServersUseCase @Inject constructor(
    private val repository: PlexTVRepository
) {

    operator fun invoke() = repository.getUserServers()
}
