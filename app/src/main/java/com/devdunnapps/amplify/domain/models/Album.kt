package com.devdunnapps.amplify.domain.models

import java.io.Serializable

data class Album(
        val artistId: String,
        val title: String,
        val artistThumb: String,
        val id: String,
        val review: String,
        val thumb: String,
        val numSongs: Int,
        val year: String,
        val artistName: String,
        val studio: String
) : Serializable
