package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.devdunnapps.amplify.domain.models.Song

@Composable
fun SongsList(
    songs: List<Song>,
    onItemClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    LazyColumn {
        items(items = songs, key = { it.id }) { song ->
            SongItem(
                onClick = { onItemClick(song) },
                song = song,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
