package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val repository: PlexTVRepository
) {

    operator fun invoke(username: String, password: String, authToken: String?) = repository.signInUser(username, password, authToken)
}
