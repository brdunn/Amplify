package com.devdunnapps.amplify.data.models

import java.io.Serializable

data class StreamDTO (
    val id: String?,
    val key: String?,
    val provider: String?,
    val format: String?,
    val streamType: Int?
) : Serializable
