package com.devdunnapps.amplify.domain.models

import java.io.Serializable

data class Song(
    val id: String,
    val albumId: String,
    val artistId: String,
    val title: String,
    val artistName: String,
    val duration: Long,
    val artistThumb: String,
    val thumb: String,
    val year: String,
    val albumName: String,
    var songUrl: String,
    val userRating: Int
) : Serializable
