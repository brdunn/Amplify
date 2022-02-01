package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.Server

data class ConnectionDTO(
    val uri: String
) {

    fun toServer() = Server(
        address = uri
    )
}
