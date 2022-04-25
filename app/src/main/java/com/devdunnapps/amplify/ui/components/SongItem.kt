package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.TimeUtils
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable{ onClick() }
    ) {
        val (artwork, title, artist, menu) = createRefs()
        val guideline = createGuidelineFromTop(0.5f)
        val context = LocalContext.current
        val artworkUrl = remember { PlexUtils.getInstance(context).getSizedImage(song.thumb, 200, 200) }

        Image(
            painter = rememberImagePainter(
                data = artworkUrl,
                imageLoader = LocalImageLoader.current,
                builder = {
                    placeholder(R.drawable.ic_albums_black_24dp)
                    error(R.drawable.ic_albums_black_24dp)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(artwork) {
                    start.linkTo(parent.start)
                }
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .fillMaxHeight()
                .aspectRatio(1f, true)
        )

        Text(
            text = song.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    bottom.linkTo(guideline)
                    width = Dimension.fillToConstraints
                }
        )

        val songDuration = TimeUtils.millisecondsToTime(song.duration)
        Text(
            text = "${song.artistName} • $songDuration",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(artist) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    top.linkTo(guideline)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Start
        )

        IconButton(
            onClick = { onItemMenuClick(song.id) },
            modifier = Modifier
                .constrainAs(menu) {
                    end.linkTo(parent.end)
                }
                .fillMaxHeight()
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
    Mdc3Theme {
        Surface {
            SongItem(
                song =song,
                onClick = {},
                onItemMenuClick = {}
            )
        }
    }
}
