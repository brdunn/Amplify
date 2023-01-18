package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.theme.Theme
import com.devdunnapps.amplify.utils.PlexUtils

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (String) -> Unit,
    onItemMenuClick: (Playlist) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick(playlist.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val resources = LocalContext.current.resources

        AsyncImage(
            model = PlexUtils.getInstance(LocalContext.current).getSizedImage(playlist.composite),
            error = painterResource(id = R.drawable.ic_album),
            fallback = painterResource(id = R.drawable.ic_album),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .fillMaxHeight()
                .aspectRatio(1f, true)
        )

        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )

            Text(
                text = resources.getQuantityString(
                    R.plurals.album_track_count,
                    playlist.numSongs,
                    playlist.numSongs
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
        }

        IconButton(
            onClick = { onItemMenuClick(playlist) }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun PlaylistItemPreview() {
    val playlist = Playlist(
        id = "null",
        title = "Song Title",
        numSongs = 10,
        summary = "",
        composite = ""
    )
    Theme {
        Surface {
            PlaylistItem(
                playlist = playlist,
                onClick = {},
                onItemMenuClick = {}
            )
        }
    }
}
