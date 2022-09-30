package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.utils.PlexUtils
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun ArtistCard(onClick: () -> Unit, artist: Artist) {
    Column(
        modifier = Modifier.clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val imageUrl = remember { PlexUtils.getInstance(context).getSizedImage(artist.thumb, 500, 500) }
        AsyncImage(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(CircleShape),
            model = imageUrl,
            placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
            error = rememberVectorPainter(Icons.Filled.AccountCircle),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            text = artist.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun ArtistCardPreview() {
    Mdc3Theme {
        ArtistCard(
            onClick = {},
            artist = Artist(
                id = "99",
                name = "Billy Joel",
                thumb = "/library/metadata/45209/thumb/1641184622",
                bio = ""
            )
        )
    }
}
