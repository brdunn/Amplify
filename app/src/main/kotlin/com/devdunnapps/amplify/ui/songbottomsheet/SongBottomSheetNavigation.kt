package com.devdunnapps.amplify.ui.songbottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.navigation.SongMenuBottomSheetRoute
import com.stefanoq21.material3.navigation.bottomSheet

fun NavGraphBuilder.songBottomSheet(
    close: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToAddToPlaylist: (String) -> Unit,
    onOpenSongAdditionalInformationBottomSheet: (Song) -> Unit
) {
    bottomSheet<SongMenuBottomSheetRoute> {
        Box(modifier = Modifier.navigationBarsPadding()) {
            SongBottomSheet(
                close = close,
                onGoToAlbumClick = onNavigateToAlbum,
                onGoToArtistClick = onNavigateToArtist,
                onAddToPlaylist = { songId ->
                    close()
                    onNavigateToAddToPlaylist(songId)
                },
                onInfoClick = onOpenSongAdditionalInformationBottomSheet
            )
        }
    }
}

fun NavController.openSongSongBottomSheet(songId: String) {
    navigate(SongMenuBottomSheetRoute(songId = songId))
}
