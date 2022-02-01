package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.composethemeadapter3.Mdc3Theme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumCard(
    onClick: () -> Unit,
    album: Album
) {
    MdcTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            elevation = 8.dp,
            onClick = onClick
        ) {
            Column {
                val context = LocalContext.current
                val imageUrl = remember { PlexUtils.getInstance(context).getSizedImage(album.thumb, 300, 300) }
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    painter = rememberImagePainter(
                        data = imageUrl,
                        imageLoader = LocalImageLoader.current,
                        builder = {
                            placeholder(R.drawable.ic_albums_black_24dp)
                            error(R.drawable.ic_albums_black_24dp)
                        }
                    ),
                    contentDescription = "Image of ${album.title}",
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
fun AlbumCardPreview() {
    Mdc3Theme {
        AlbumCard(
            onClick = {},
            album = Album(
                id = "99",
                title = "52nd Street",
                thumb = "/library/metadata/45209/thumb/1641184622",
                artistId = "",
                artistThumb = "",
                review = "",
                numSongs = 0,
                year = "",
                artistName = "",
                studio = ""
            )
        )
    }
}
