package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devdunnapps.amplify.ui.theme.Theme

@Composable
internal fun NowPlayingCollapsed(
    albumArtUrl: String,
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onPlayPauseClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .requiredHeight(64.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        AsyncImage(
            model = albumArtUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(48.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }

        IconButton(onClick = onPlayPauseClick) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null
            )
        }

        IconButton(onClick = onSkipClick) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun NowPlayingCollapsedPreview() {
    Theme {
        NowPlayingCollapsed(
            albumArtUrl = "",
            title = "Vienna",
            subtitle = "Billy Joel",
            isPlaying = true,
            onPlayPauseClick = {},
            onSkipClick = {}
        )
    }
}
