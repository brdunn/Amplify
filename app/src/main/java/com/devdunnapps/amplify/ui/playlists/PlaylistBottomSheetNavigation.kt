package com.devdunnapps.amplify.ui.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.playlistBottomSheet(
    onDeletePlaylistClicked: () -> Unit
) {
    bottomSheet(
        route = "playlists/{playlistId}/details",
        arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            PlaylistBottomSheet(
                onDeletePlaylistClicked = onDeletePlaylistClicked
            )
        }
    }
}

fun NavController.openPlaylistBottomSheet(playlistId: String) {
    navigate("playlists/$playlistId/details")
}
