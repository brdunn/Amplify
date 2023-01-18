package com.devdunnapps.amplify.ui.songbottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devdunnapps.amplify.ui.nowplaying.LyricsBottomSheet
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.songBottomSheet(
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onOpenLyricsBottomSheet: (String) -> Unit
) {
    bottomSheet(
        route = "songs/{songId}/details",
        arguments = listOf(navArgument("songId") { type = NavType.StringType })
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            SongBottomSheet(
                onPlayNextClick = { /*TODO*/ },
                onAddToQueueClick = { /*TODO*/ },
                onGoToAlbumClick = onNavigateToAlbum,
                onGoToArtistClick = onNavigateToArtist,
                onAddToPlaylist = {},
                onLyricsClick = onOpenLyricsBottomSheet,
                onInfoClick = {},
                refreshPreviousScreen = {}
            )
        }
    }
}

fun NavController.openSongSongBottomSheet(songId: String) {
    navigate("songs/$songId/details")
}
