package com.devdunnapps.amplify.ui.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devdunnapps.amplify.ui.navigation.SearchRoute

fun NavGraphBuilder.searchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToPlaylist: (String) -> Unit,
    onOpenSongMenu: (String) -> Unit
) {
    composable<SearchRoute> {
        SearchRoute(
            onNavigateUp = onNavigateBack,
            onArtistClick = onNavigateToArtist,
            onAlbumClick = onNavigateToAlbum,
            onPlaylistClick = onNavigateToPlaylist,
            onSongMenuClick = onOpenSongMenu
        )
    }
}

fun NavController.navigateToSearch() {
    navigate(route = SearchRoute)
}
