package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun AlbumCard(onClick: () -> Unit, album: Album, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val context = LocalContext.current
        val imageUrl = remember { PlexUtils.getInstance(context).getSizedImage(album.thumb, 300, 300) }
        AsyncImage(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp)),
            model = imageUrl,
            placeholder = painterResource(R.drawable.ic_album),
            error = painterResource(R.drawable.ic_album),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
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
