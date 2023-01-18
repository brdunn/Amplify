package com.devdunnapps.amplify.ui.nowplaying

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.lyricsBottomSheet(
) {
    bottomSheet(
        route = "songs/{songId}/lyrics",
        arguments = listOf(navArgument("songId") { type = NavType.StringType })
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            LyricsBottomSheetRoute()
        }
    }
}

fun NavController.openLyricsBottomSheet(songId: String) {
    navigate("songs/$songId/lyrics")
}
