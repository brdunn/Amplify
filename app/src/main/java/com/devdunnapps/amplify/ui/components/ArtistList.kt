package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.domain.models.Artist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistList(
    artists: List<Artist>,
    onItemClick: (String) -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 2),
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = artists
        ) { artist ->
            ArtistCard(
                onClick = { onItemClick(artist.id) },
                artist = artist
            )
        }
    }
}
