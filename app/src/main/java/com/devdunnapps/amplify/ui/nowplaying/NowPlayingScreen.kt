package com.devdunnapps.amplify.ui.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devdunnapps.amplify.ui.utils.DynamicThemePrimaryColorsFromImage
import com.devdunnapps.amplify.ui.utils.getCurrentSizeClass
import com.devdunnapps.amplify.ui.utils.rememberDominantColorState
import com.devdunnapps.amplify.ui.utils.whenTrue
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.devdunnapps.amplify.utils.TimeUtils

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel(),
    onCollapseNowPlaying: () -> Unit,
    onNowPlayingMenuClick: (String) -> Unit
) {
    val metadata = viewModel.metadata.collectAsState().value

    // Only display content if something is playing
    if (metadata == NOTHING_PLAYING)
        return

    val songDurationMillis = viewModel.duration.collectAsState().value
    val mediaPosition = viewModel.mediaPosition.collectAsState().value
    val isPlaying = viewModel.isPlaying.collectAsState().value
    val shuffleModel = viewModel.shuffleMode.collectAsState().value
    val repeatMode = viewModel.repeatMode.collectAsState().value

    NowPlayingHeader(
        artworkUrl = metadata.artworkUri.toString(),
        title = metadata.title.toString(),
        subtitle = metadata.artist.toString(),
        onSeekToPosition = { viewModel.seekTo((it * 1000).toLong()) },
        mediaPosition = mediaPosition,
        songDurationMillis = songDurationMillis,
        isPlaying = isPlaying,
        shuffleMode = shuffleModel,
        repeatMode = repeatMode,
        onToggleShuffleClick = viewModel::toggleShuffleState,
        onToggleRepeatClick = viewModel::toggleRepeatState,
        onTogglePlayPause = viewModel::togglePlayingState,
        onSkipPrevious = viewModel::skipToPrevious,
        onSkipNext = viewModel::skipToNext,
        onCollapseNowPlaying = onCollapseNowPlaying,
        onMenuClick = {
//            onNowPlayingMenuClick(viewModel.metadata.value.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
        }
    )
}

@Composable
private fun NowPlayingHeader(
    artworkUrl: String,
    title: String,
    subtitle: String,
    onSeekToPosition: (Float) -> Unit,
    mediaPosition: Long,
    songDurationMillis: Long,
    isPlaying: Boolean,
    shuffleMode: Boolean,
    repeatMode: Int,
    onToggleShuffleClick: () -> Unit,
    onToggleRepeatClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onCollapseNowPlaying: () -> Unit,
    onMenuClick: () -> Unit
) {
    val dominantColorState = rememberDominantColorState()

    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        LaunchedEffect(artworkUrl) {
            dominantColorState.updateColorsFromImageUrl(artworkUrl)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 32.dp)
        ) {
            NowPlayingTopBar(onCollapseNowPlaying = onCollapseNowPlaying, onMenuClick = onMenuClick)

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artworkUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .whenTrue(getCurrentSizeClass() != WindowWidthSizeClass.Compact) { widthIn(max = 350.dp) }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                NowPlayingProgressBar(
                    onSeekToPosition = onSeekToPosition,
                    mediaPosition = mediaPosition,
                    songDurationMillis = songDurationMillis
                )

                MediaButtonsRow(
                    isPlaying = isPlaying,
                    shuffleMode = shuffleMode,
                    repeatMode = repeatMode,
                    onToggleShuffleClick = onToggleShuffleClick,
                    onToggleRepeatClick = onToggleRepeatClick,
                    onTogglePlayPause = onTogglePlayPause,
                    onSkipPrevious = onSkipPrevious,
                    onSkipNext = onSkipNext
                )
            }
        }
    }
}

@Composable
private fun NowPlayingTopBar(onCollapseNowPlaying: () -> Unit, onMenuClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onCollapseNowPlaying) {
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun NowPlayingProgressBar(
    onSeekToPosition: (Float) -> Unit,
    mediaPosition: Long,
    songDurationMillis: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        var sliderPosition by remember(mediaPosition) { mutableStateOf((mediaPosition / 1000).toFloat()) }
        Slider(
            value = sliderPosition,
            valueRange = 0f..(songDurationMillis / 1000).toFloat(),
            onValueChange = {
                sliderPosition = it
                onSeekToPosition(it)
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = TimeUtils.millisecondsToTime(mediaPosition),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.offset(y = (-6).dp)
            )

            Text(
                text = TimeUtils.millisecondsToTime(songDurationMillis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.offset(y = (-6).dp)
            )
        }
    }
}

@Composable
private fun MediaButtonsRow(
    isPlaying: Boolean,
    shuffleMode: Boolean,
    repeatMode: Int,
    onToggleShuffleClick: () -> Unit,
    onToggleRepeatClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowSpacing = if (getCurrentSizeClass() == WindowWidthSizeClass.Compact) 8.dp else 16.dp
    Row(
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = onToggleShuffleClick) {
            Icon(
                imageVector = if (shuffleMode) Icons.Filled.ShuffleOn else Icons.Filled.Shuffle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(onClick = onSkipPrevious) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }

        FloatingActionButton(
            shape = CircleShape,
            onClick = onTogglePlayPause,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null
            )
        }

        IconButton(onClick = onSkipNext) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }

        val repeatModeIcon = when (repeatMode) {
            Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
            Player.REPEAT_MODE_ALL -> Icons.Filled.RepeatOn
            else -> Icons.Filled.Repeat
        }
        IconButton(onClick = onToggleRepeatClick) {
            Icon(
                imageVector = repeatModeIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
