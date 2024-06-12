package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.SignInModel

data class SignInResponseDTO(
    val authToken: String
) {
    fun toSignInModel() = SignInModel(
        authToken = authToken
    )
}
