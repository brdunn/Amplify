package com.devdunnapps.amplify.domain.models

data class Server (
    val address: String,
    val localConnectionsOnly: Boolean,
    val proxyConnectionsAllowed: Boolean
)
