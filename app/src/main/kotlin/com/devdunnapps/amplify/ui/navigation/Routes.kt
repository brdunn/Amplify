package com.devdunnapps.amplify.ui.navigation

import com.devdunnapps.amplify.domain.models.Song
import kotlinx.serialization.Serializable

@Serializable
data object MainGraphRoute

@Serializable
data object HomeRoute

@Serializable
data object ArtistsRoute

@Serializable
data object AlbumsRoute

@Serializable
data object SongsRoute

@Serializable
data object PlaylistsRoute

@Serializable
data class ArtistRoute(val artistId: String)

@Serializable
data class AlbumRoute(val albumId: String)

@Serializable
data class PlaylistRoute(val playlistId: String)

@Serializable
data class ArtistAllAlbumsRoute(val artistId: String, val isSinglesEPs: Boolean)

@Serializable
data class ArtistAllSongsRoute(val artistId: String)

@Serializable
data class SongMenuBottomSheetRoute(val songId: String)

@Serializable
data class SongAdditionalInformationRoute(
    val title: String,
    val thumb: String,
    val playCount: Int
)

@Serializable
data class AddToPlaylistRoute(val songId: String)

@Serializable
data object SearchRoute

@Serializable
data object NowPlayingRoute

@Serializable
data object SettingsRoute

@Serializable
data object AboutRoute
