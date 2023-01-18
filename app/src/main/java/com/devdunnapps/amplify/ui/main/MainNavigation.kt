package com.devdunnapps.amplify.ui.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devdunnapps.amplify.ui.album.albumScreen
import com.devdunnapps.amplify.ui.album.navigateToAlbum
import com.devdunnapps.amplify.ui.albums.AlbumsRoute
import com.devdunnapps.amplify.ui.artist.artistScreen
import com.devdunnapps.amplify.ui.artist.navigateToArtist
import com.devdunnapps.amplify.ui.artists.ArtistsRoute
import com.devdunnapps.amplify.ui.navigation.AmplifyNavigationDestination
import com.devdunnapps.amplify.ui.nowplaying.lyricsBottomSheet
import com.devdunnapps.amplify.ui.nowplaying.openLyricsBottomSheet
import com.devdunnapps.amplify.ui.playlist.navigateToPlaylist
import com.devdunnapps.amplify.ui.playlist.playlistScreen
import com.devdunnapps.amplify.ui.playlists.PlaylistsRoute
import com.devdunnapps.amplify.ui.playlists.openPlaylistBottomSheet
import com.devdunnapps.amplify.ui.playlists.playlistBottomSheet
import com.devdunnapps.amplify.ui.songbottomsheet.openSongSongBottomSheet
import com.devdunnapps.amplify.ui.songbottomsheet.songBottomSheet
import com.devdunnapps.amplify.ui.songs.SongsRoute

object ArtistsDestination : AmplifyNavigationDestination {
    override val route = "artists"
    override val destination = "artists_destination"
}

object AlbumsDestination : AmplifyNavigationDestination {
    override val route = "albums"
    override val destination = "albums_destination"
}

object SongsDestination : AmplifyNavigationDestination {
    override val route = "songs"
    override val destination = "songs_destination"
}

object PlaylistsDestination : AmplifyNavigationDestination {
    override val route = "playlists"
    override val destination = "playlist_destination"
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    navigation(
        route = "main_route",
        startDestination = ArtistsDestination.route
    ) {
        composable(route = ArtistsDestination.route) {
            ArtistsRoute(onArtistClick = { artistId -> navController.navigateToArtist(artistId) })
        }

        composable(route = AlbumsDestination.route) {
            AlbumsRoute(onAlbumClick = { albumId -> navController.navigateToAlbum(albumId) })
        }

        composable(route = SongsDestination.route) {
            SongsRoute(onSongMenuClick = { songId -> navController.openSongSongBottomSheet(songId) })
        }

        composable(route = PlaylistsDestination.route) {
            PlaylistsRoute(
                onPlaylistClick = { playlistId -> navController.navigateToPlaylist(playlistId) },
                onPlaylistMenuClick = { playlistId -> navController.openPlaylistBottomSheet(playlistId) }
            )
        }

        artistScreen(
            onNavigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) },
            onNavigateToAllArtistAlbums = {},
            onNavigateToAllArtistEPsSingles = {},
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) },
            onNavigateToAllArtistSongs = {}
        )

        albumScreen(
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) }
        )

        playlistScreen(
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) }
        )

        songBottomSheet(
            onNavigateToAlbum = {albumId -> navController.navigateToAlbum(albumId) },
            onNavigateToArtist = { artistId -> navController.navigateToArtist(artistId) },
            onOpenLyricsBottomSheet = { songId -> navController.openLyricsBottomSheet(songId) }
        )

        playlistBottomSheet(
            onDeletePlaylistClicked = {}
        )

        lyricsBottomSheet()
    }
}
