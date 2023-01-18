package com.devdunnapps.amplify.ui.playlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.navigation.PlaylistRoute

fun NavGraphBuilder.playlistScreen(onNavigateBack: () -> Unit, onOpenSongMenu: (String) -> Unit) {
    composable<PlaylistRoute> {
        PlaylistRoute(onBackClick = onNavigateBack, onSongMenuClick = { onOpenSongMenu(it.id) })
    }
}

fun NavController.navigateToPlaylist(playlistId: String) {
    navigate(PlaylistRoute(playlistId = playlistId))
}
