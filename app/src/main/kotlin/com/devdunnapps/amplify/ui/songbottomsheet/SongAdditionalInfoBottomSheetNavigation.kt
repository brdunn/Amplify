package com.devdunnapps.amplify.ui.songbottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.devdunnapps.amplify.ui.navigation.SongAdditionalInformationRoute
import com.stefanoq21.material3.navigation.bottomSheet

fun NavGraphBuilder.songAdditionalInfoBottomSheet() {
    bottomSheet<SongAdditionalInformationRoute> { navBackStackEntry ->
        val songAdditionalInformationRoute: SongAdditionalInformationRoute = navBackStackEntry.toRoute()

        Box(modifier = Modifier.navigationBarsPadding()) {
            SongAdditionalInfoBottomSheet(
                title = songAdditionalInformationRoute.title,
                thumb = songAdditionalInformationRoute.thumb,
                playCount = songAdditionalInformationRoute.playCount
            )
        }
    }
}

fun NavController.openSongAdditionalInfoBottomSheet(
    title: String,
    thumb: String,
    playCount: Int
) {
    navigate(
        SongAdditionalInformationRoute(
            title = title,
            thumb = thumb,
            playCount = playCount
        )
    )
}