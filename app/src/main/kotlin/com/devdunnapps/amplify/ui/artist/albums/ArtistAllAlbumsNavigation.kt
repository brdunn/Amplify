package com.devdunnapps.amplify.ui.artist.albums

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devdunnapps.amplify.ui.navigation.ArtistAllAlbumsRoute

fun NavGraphBuilder.artistAllAlbumsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAlbum: (String) -> Unit
) {
    composable<ArtistAllAlbumsRoute> { backStackEntry ->
        val isSinglesEPs = backStackEntry.toRoute<ArtistAllAlbumsRoute>().isSinglesEPs

        ArtistAllAlbumsRoute(
            isSinglesEPs = isSinglesEPs,
            onBackClick = onNavigateBack,
            onAlbumClick = onNavigateToAlbum,
        )
    }
}

fun NavController.navigateToArtistAllAlbums(artistId: String, isSinglesEPs: Boolean) {
    navigate(ArtistAllAlbumsRoute(artistId = artistId, isSinglesEPs = isSinglesEPs))
}