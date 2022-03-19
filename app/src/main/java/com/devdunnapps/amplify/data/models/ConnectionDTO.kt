package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.Server

data class ConnectionDTO(
    val uri: String,
    val local: Boolean,
    val relay: Boolean
) {

    fun toServer() = Server(
        address = uri,
        localConnectionsOnly = local,
        proxyConnectionsAllowed = relay
    )
}
