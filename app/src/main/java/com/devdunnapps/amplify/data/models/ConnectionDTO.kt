package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.Server

data class ConnectionDTO(
    val uri: String,
    val local: Boolean,
    val relay: Boolean
) {

    fun toServer(serverName: String) = Server(
        name = serverName,
        address = uri,
        localConnectionsOnly = local,
        proxyConnectionsAllowed = relay
    )
}
