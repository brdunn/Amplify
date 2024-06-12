package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.User

data class UserDTO(
    val username: String,
    val friendlyName: String?,
    val thumb: String
) {
    fun toUser() = User(
        username = username,
        displayName = friendlyName,
        avatar = thumb
    )
}
