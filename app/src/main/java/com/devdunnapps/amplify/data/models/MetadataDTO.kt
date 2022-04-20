package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MetadataDTO(
    val parentRatingKey: String?,
    val grandparentRatingKey: String?,
    val title: String?,
    val grandparentTitle: String?,
    val duration: Long?,
    @SerializedName("Media") val media: List<MediaDTO>?,
    @SerializedName("Directory") val directory: List<DirectoryDTO>?,
    val parentThumb: String?,
    val ratingKey: String?,
    val summary: String?,
    val thumb: String?,
    val leafCount: Int?,
    val type: String?,
    val year: String?,
    val parentTitle: String?,
    val studio: String?,
    val playlistItemID: String?,
    val librarySectionID: String?,
    val userRating: Int?,
    val viewCount: Int?
) : Serializable {

    fun toAlbum() = Album(
        artistId = parentRatingKey ?: "",
        title = title!!,
        artistThumb = parentThumb ?: "",
        id = ratingKey!!,
        review = summary,
        thumb = thumb ?: "",
        numSongs = leafCount ?: 0,
        year = year ?: "",
        artistName = parentTitle!!,
        studio = studio ?: ""
    )

    fun toArtist() = Artist(
        id = ratingKey!!,
        name = title!!,
        thumb = thumb ?: "",
        bio = summary
    )

    fun toSong() = Song(
        id = ratingKey!!,
        albumId = parentRatingKey!!,
        artistId = grandparentRatingKey!!,
        title = title!!,
        artistName = grandparentTitle!!,
        duration = duration!!,
        artistThumb = parentThumb ?: "",
        thumb = thumb ?: "",
        year = year ?: "",
        albumName = parentTitle!!,
        songUrl = media!![0].part!![0].key!!,
        userRating = userRating ?: Rating.THUMB_GONE,
        playCount = viewCount
    )

    fun toPlaylist() = Playlist(
        id = ratingKey!!,
        title = title!!,
        summary = summary!!,
        numSongs = leafCount!!
    )
}
