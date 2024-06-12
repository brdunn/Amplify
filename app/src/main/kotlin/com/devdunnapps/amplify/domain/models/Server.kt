package com.devdunnapps.amplify.domain.models

data class Server(
    val name: String,
    val address: String,
    val localConnectionsOnly: Boolean,
    val proxyConnectionsAllowed: Boolean,
    val accessToken: String
)
