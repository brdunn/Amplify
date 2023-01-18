package com.devdunnapps.amplify.ui.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devdunnapps.amplify.ui.addtoplaylist.addToPlaylistBottomSheet
import com.devdunnapps.amplify.ui.addtoplaylist.openAddToPlaylistBottomSheet
import com.devdunnapps.amplify.ui.album.albumScreen
import com.devdunnapps.amplify.ui.album.navigateToAlbum
import com.devdunnapps.amplify.ui.albums.AlbumsRoute
import com.devdunnapps.amplify.ui.artist.albums.artistAllAlbumsScreen
import com.devdunnapps.amplify.ui.artist.albums.navigateToArtistAllAlbums
import com.devdunnapps.amplify.ui.artist.artistScreen
import com.devdunnapps.amplify.ui.artist.navigateToArtist
import com.devdunnapps.amplify.ui.artist.songs.artistAllSongsScreen
import com.devdunnapps.amplify.ui.artist.songs.navigateToArtistAllSongs
import com.devdunnapps.amplify.ui.artists.ArtistsRoute
import com.devdunnapps.amplify.ui.home.HomeRoute
import com.devdunnapps.amplify.ui.navigation.AlbumsRoute
import com.devdunnapps.amplify.ui.navigation.ArtistsRoute
import com.devdunnapps.amplify.ui.navigation.HomeRoute
import com.devdunnapps.amplify.ui.navigation.MainGraphRoute
import com.devdunnapps.amplify.ui.navigation.PlaylistsRoute
import com.devdunnapps.amplify.ui.navigation.SongsRoute
import com.devdunnapps.amplify.ui.playlist.navigateToPlaylist
import com.devdunnapps.amplify.ui.playlist.playlistScreen
import com.devdunnapps.amplify.ui.playlists.PlaylistsRoute
import com.devdunnapps.amplify.ui.search.searchScreen
import com.devdunnapps.amplify.ui.songbottomsheet.openSongAdditionalInfoBottomSheet
import com.devdunnapps.amplify.ui.songbottomsheet.openSongSongBottomSheet
import com.devdunnapps.amplify.ui.songbottomsheet.songAdditionalInfoBottomSheet
import com.devdunnapps.amplify.ui.songbottomsheet.songBottomSheet
import com.devdunnapps.amplify.ui.songs.SongsRoute

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    topBarActions: @Composable RowScope.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        route = MainGraphRoute::class,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeRoute(
                topBarActions = topBarActions,
                navigateToArtist = { artistId -> navController.navigateToArtist(artistId) },
                navigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) },
                onSongMenuClick = { songId -> navController.openSongSongBottomSheet(songId) }
            )
        }

        composable<ArtistsRoute> {
            ArtistsRoute(
                topBarActions = topBarActions,
                onArtistClick = { artistId -> navController.navigateToArtist(artistId) }
            )
        }

        composable<AlbumsRoute> {
            AlbumsRoute(
                topBarActions = topBarActions,
                onAlbumClick = { albumId -> navController.navigateToAlbum(albumId) }
            )
        }

        composable<SongsRoute> {
            SongsRoute(
                topBarActions = topBarActions,
                onSongMenuClick = { songId -> navController.openSongSongBottomSheet(songId) }
            )
        }

        composable<PlaylistsRoute> {
            PlaylistsRoute(
                topBarActions = topBarActions,
                onPlaylistClick = { playlistId -> navController.navigateToPlaylist(playlistId) }
            )
        }

        artistScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) },
            onNavigateToAllArtistAlbums = { artistId ->
                navController.navigateToArtistAllAlbums(artistId, false)
            },
            onNavigateToAllArtistEPsSingles = { artistId ->
                navController.navigateToArtistAllAlbums(artistId, true)
            },
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) },
            onNavigateToAllArtistSongs = { artistId ->
                navController.navigateToArtistAllSongs(artistId)
            }
        )

        artistAllAlbumsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) }
        )

        artistAllSongsScreen(
            onNavigateBack = { navController.popBackStack() },
            onSongMenuClick = { songId -> navController.openSongSongBottomSheet(songId) }
        )

        albumScreen(
            onNavigateBack = { navController.popBackStack() },
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) }
        )

        playlistScreen(
            onNavigateBack = { navController.popBackStack() },
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) }
        )

        songBottomSheet(
            close = { navController.popBackStack() },
            onNavigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) },
            onNavigateToArtist = { artistId -> navController.navigateToArtist(artistId) },
            onOpenSongAdditionalInformationBottomSheet = { song ->
                navController.popBackStack()
                navController.openSongAdditionalInfoBottomSheet(
                    title = song.title,
                    thumb = song.thumb,
                    playCount = song.playCount
                )
            },
            onNavigateToAddToPlaylist = { songId ->
                navController.popBackStack()
                navController.openAddToPlaylistBottomSheet(songId)
            }
        )

        addToPlaylistBottomSheet(close = { navController.popBackStack() })

        songAdditionalInfoBottomSheet()

        searchScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToArtist = { artistId -> navController.navigateToArtist(artistId) },
            onNavigateToAlbum = { albumId -> navController.navigateToAlbum(albumId) },
            onNavigateToPlaylist = { playlistId -> navController.navigateToPlaylist(playlistId) },
            onOpenSongMenu = { songId -> navController.openSongSongBottomSheet(songId) }
        )
    }
}
