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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (String) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick(playlist.id) }
    ) {
        val (artwork, title, numSongs, menu) = createRefs()
        val guideline = createGuidelineFromTop(0.5f)
        val resources = LocalContext.current.resources

        Image(
            painter = painterResource(id = R.drawable.ic_albums_black_24dp),
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
            text = playlist.title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    bottom.linkTo(guideline)
                    width = Dimension.fillToConstraints
                }
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
            modifier = Modifier
                .constrainAs(numSongs) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    top.linkTo(guideline)
                    width = Dimension.fillToConstraints
                }
        )

        IconButton(
            onClick = { onItemMenuClick(playlist.id) },
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
fun PlaylistItemPreview() {
    val playlist = Playlist(
        id = "null",
        title = "Song Title",
        numSongs = 10,
        summary = ""
    )
    Mdc3Theme {
        Surface {
            PlaylistItem(
                playlist = playlist,
                onClick = {},
                onItemMenuClick = {}
            )
        }
    }
}
