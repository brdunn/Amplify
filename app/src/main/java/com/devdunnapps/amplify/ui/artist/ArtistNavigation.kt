package com.devdunnapps.amplify.ui.artist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val ARTIST_ID_ARG = "artistId"

fun NavGraphBuilder.artistScreen(
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToAllArtistAlbums: () -> Unit,
    onNavigateToAllArtistEPsSingles: () -> Unit,
    onOpenSongMenu: (String) -> Unit,
    onNavigateToAllArtistSongs: () -> Unit
) {
    composable(
        route = "artists/{artistId}",
        arguments = listOf(navArgument(ARTIST_ID_ARG) { type = NavType.StringType })
    ) {
        ArtistRoute(
            onAlbumClick = onNavigateToAlbum,
            onViewAllAlbumsClick = onNavigateToAllArtistAlbums,
            onViewAllSinglesEPsClick = onNavigateToAllArtistEPsSingles,
            onSongMenuClick = onOpenSongMenu,
            onViewAllSongsClick = onNavigateToAllArtistSongs
        )
    }
}

fun NavController.navigateToArtist(artistId: String) {
    navigate("artists/$artistId")
}
