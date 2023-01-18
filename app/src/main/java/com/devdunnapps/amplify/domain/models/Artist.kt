package com.devdunnapps.amplify.domain.models

import java.io.Serializable

data class Artist(
        val id: String,
        val name: String,
        val thumb: String,
        val art: String?,
        val bio: String?,
) : Serializable
