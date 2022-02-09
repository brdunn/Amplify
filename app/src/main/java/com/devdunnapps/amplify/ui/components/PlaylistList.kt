package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.devdunnapps.amplify.domain.models.Playlist

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onItemClick: (String) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    LazyColumn {
        items(
            items = playlists,
            key = { it.id }
        ) { playlist ->
            PlaylistItem(
                onClick = onItemClick,
                playlist = playlist,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
