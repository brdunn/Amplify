package com.devdunnapps.amplify.ui.addtoplaylist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.devdunnapps.amplify.ui.navigation.AddToPlaylistRoute
import com.stefanoq21.material3.navigation.bottomSheet

fun NavGraphBuilder.addToPlaylistBottomSheet(close: () -> Unit) {
    bottomSheet<AddToPlaylistRoute> {
        Box(modifier = Modifier.navigationBarsPadding()) {
            AddToPlaylistBottomSheet(close = close)
        }
    }
}

fun NavController.openAddToPlaylistBottomSheet(songId: String) {
    navigate(AddToPlaylistRoute(songId = songId))
}
