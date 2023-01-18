package com.devdunnapps.amplify.ui.album

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.albumScreen(
    onOpenSongMenu: (String) -> Unit
) {
    composable(
        route = "albums/{albumId}",
        arguments = listOf(navArgument("albumId") { type = NavType.StringType })
    ) {
        AlbumRoute(onSongMenuClick = onOpenSongMenu)
    }
}

fun NavController.navigateToAlbum(albumId: String) {
    navigate("albums/$albumId")
}
