package com.devdunnapps.amplify.ui.album

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devdunnapps.amplify.ui.navigation.AlbumRoute

fun NavGraphBuilder.albumScreen(onNavigateBack: () -> Unit, onOpenSongMenu: (String) -> Unit) {
    composable<AlbumRoute> {
        AlbumRoute(onBackClick = onNavigateBack, onSongMenuClick = onOpenSongMenu)
    }
}

fun NavController.navigateToAlbum(albumId: String) {
    navigate(AlbumRoute(albumId = albumId))
}
