package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.Server

data class ResourceDTO(
    val name: String,
    val provides: String,
    val connections: List<ConnectionDTO>
) {

    fun toServer() = Server(
        address = connections[0].uri
    )
}
