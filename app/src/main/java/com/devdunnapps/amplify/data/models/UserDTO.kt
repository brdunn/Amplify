package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.User

data class UserDTO (
    val authToken: String
) {

    fun toUser() = User(
        authToken = authToken
    )
}
