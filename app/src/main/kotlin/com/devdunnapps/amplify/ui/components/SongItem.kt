package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.theme.Theme
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.TimeUtils

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val artworkUrl = remember { PlexUtils.getInstance(context).getSizedImage(song.thumb, 200, 200) }

        AsyncImage(
            model = artworkUrl,
            placeholder = painterResource(R.drawable.ic_album),
            error = painterResource(R.drawable.ic_album),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .fillMaxHeight()
                .aspectRatio(1f, true)
                .clip(shape = RoundedCornerShape(4.dp))
        )

        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            val songDuration = TimeUtils.millisecondsToTime(song.duration)
            Text(
                text = "${song.artistName} • $songDuration",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        IconButton(
            onClick = { onItemMenuClick(song.id) }
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
fun SongItemPreview() {
    val song = Song(
        id = "null",
        albumId = "",
        artistId = "",
        albumName = "",
        artistName = "Artist Name",
        artistThumb = "",
        duration = 142000,
        songUrl = "",
        thumb = "",
        title = "Song Title",
        year = "",
        userRating = 0,
        playCount = 10
    )
    Theme {
        Surface {
            SongItem(
                song = song,
                onClick = {},
                onItemMenuClick = {}
            )
        }
    }
}
