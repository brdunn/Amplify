package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun AlbumHeader(album: Album) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        val context = LocalContext.current
        val imageUrl = remember { PlexUtils.getInstance(context).getSizedImage(album.thumb, 500, 500) }
        Image(
            modifier = Modifier.weight(1F),
            painter = rememberImagePainter(
                data = imageUrl,
                imageLoader = LocalImageLoader.current,
                builder = {
                    placeholder(R.drawable.ic_albums_black_24dp)
                    error(R.drawable.ic_albums_black_24dp)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .weight(1F)
                .aspectRatio(1F, false)
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                val textStyleBody1 = MaterialTheme.typography.displayMedium
                var textStyle by remember { mutableStateOf(textStyleBody1) }
                var readyToDraw by remember { mutableStateOf(false)}

                Text(
                    text = album.title,
                    style = textStyle,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.drawWithContent {
                        if (readyToDraw) drawContent()
                    },
                    onTextLayout = { result ->
                        if (result.didOverflowHeight) {
                            textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                        } else {
                            readyToDraw = true
                        }
                    }
                )
            }

            Text(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                text = album.artistName + " • " + album.year
            )
        }
    }
}

@Composable
fun PlayControls(onPlayClicked: () -> Unit, onShuffleClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier.width(150.dp),
            onClick = onPlayClicked,
        ) {
            Text(text = "Play")
        }

        Button(
            modifier = Modifier.width(150.dp),
            onClick = onShuffleClicked,
        ) {
            Text(text = "Shuffle")
        }
    }
}

@Composable
fun AlbumPage(album: Album, songs: List<Song>) {
    Column {
        AlbumHeader(album = album)

        PlayControls({}, {})

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val resources = LocalContext.current.resources
            Text(
                text = resources.getQuantityString(R.plurals.album_track_count, album.numSongs, album.numSongs)
            )

            Text(
                text = "XX minutes"
            )
        }

        LazyColumn {
            itemsIndexed(
                items = songs
            ) { index, song ->
                AlbumSong(
                    song = song,
                    albumPos = index + 1,
                    onClick = { /*TODO*/ },
                    onItemMenuClick = {}
                )
            }
        }

        Text(
            text = album.studio,
            modifier = Modifier.padding(16.dp)
        )

        ExpandableText()
    }
}

@Composable
fun AlbumSong(
    song: Song,
    albumPos: Int,
    onClick: () -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() }
    ) {
        val (artwork, title, artist, menu) = createRefs()
        val guideline = createGuidelineFromTop(0.5f)

        Box(
            modifier = Modifier
                .constrainAs(artwork) {
                    start.linkTo(parent.start)
                }
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .fillMaxHeight()
                .aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = albumPos.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

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

        Text(
            text = song.artistName,
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
fun PreviewAlbumHeader() {
    Mdc3Theme {
        Surface {
            val album = Album(
                artistId = "0",
                artistName = "Billy Joel",
                artistThumb = "",
                id = "0",
                numSongs = 10,
                review = "",
                studio = "Columbia",
                thumb = "",
                title = "Complete Albums Collection",
                year = "2000"
            )

            val songs = listOf(
                Song(
                    year = "2000",
                    title = "Summer Highland Falls",
                    thumb = "",
                    id = "",
                    artistThumb = "",
                    artistName = "Billy Joel",
                    artistId = "",
                    albumId = "",
                    albumName = "Complete Albums Collection",
                    duration = 56000,
                    songUrl = "",
                    userRating = 10
                )
            )

            AlbumPage(album, songs)
        }
    }
}
