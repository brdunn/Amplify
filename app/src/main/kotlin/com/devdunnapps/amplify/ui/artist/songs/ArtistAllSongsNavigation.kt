package com.devdunnapps.amplify.ui.artist.songs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devdunnapps.amplify.ui.navigation.ArtistAllSongsRoute

fun NavGraphBuilder.artistAllSongsScreen(
    onNavigateBack: () -> Unit,
    onSongMenuClick: (String) -> Unit
) {
    composable<ArtistAllSongsRoute> {
        ArtistAllSongsRoute(
            onBackClick = onNavigateBack,
            onSongMenuClick = onSongMenuClick
        )
    }
}

fun NavController.navigateToArtistAllSongs(artistId: String) {
    navigate(ArtistAllSongsRoute(artistId))
}