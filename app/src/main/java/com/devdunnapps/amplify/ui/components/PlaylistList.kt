package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import com.devdunnapps.amplify.domain.models.Playlist

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onItemClick: (String) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(items = playlists, key = { it.id }) { playlist ->
            PlaylistItem(
                onClick = onItemClick,
                playlist = playlist,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
