package com.devdunnapps.amplify.ui.playlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.playlistScreen(
    onOpenSongMenu: (String) -> Unit
) {
    composable(
        route = "playlists/{playlistId}",
        arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
    ) {
        PlaylistRoute(onSongMenuClick = { onOpenSongMenu(it.id) })
    }
}

fun NavController.navigateToPlaylist(playlistId: String) {
    navigate("playlists/$playlistId")
}
