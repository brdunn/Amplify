package com.devdunnapps.amplify.data.models

data class ResourceDTO(
    val name: String,
    val provides: String,
    val accessToken: String?,
    val connections: List<ConnectionDTO>
)
