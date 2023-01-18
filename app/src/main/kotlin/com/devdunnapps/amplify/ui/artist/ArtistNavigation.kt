package com.devdunnapps.amplify.ui.artist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devdunnapps.amplify.ui.navigation.ArtistRoute

fun NavGraphBuilder.artistScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToAllArtistAlbums: (String) -> Unit,
    onNavigateToAllArtistEPsSingles: (String) -> Unit,
    onOpenSongMenu: (String) -> Unit,
    onNavigateToAllArtistSongs: (String) -> Unit
) {
    composable<ArtistRoute> { backStackEntry ->
        val artistRoute: ArtistRoute = backStackEntry.toRoute()

        ArtistRoute(
            onBackClick = onNavigateBack,
            onAlbumClick = onNavigateToAlbum,
            onViewAllAlbumsClick = { onNavigateToAllArtistAlbums(artistRoute.artistId) },
            onViewAllSinglesEPsClick = { onNavigateToAllArtistEPsSingles(artistRoute.artistId) },
            onSongMenuClick = onOpenSongMenu,
            onViewAllSongsClick = { onNavigateToAllArtistSongs(artistRoute.artistId) }
        )
    }
}

fun NavController.navigateToArtist(artistId: String) {
    navigate(ArtistRoute(artistId))
}
