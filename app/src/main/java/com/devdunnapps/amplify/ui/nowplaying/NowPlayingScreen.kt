package com.devdunnapps.amplify.ui.nowplaying

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devdunnapps.amplify.ui.utils.DynamicThemePrimaryColorsFromImage
import com.devdunnapps.amplify.ui.utils.rememberDominantColorState
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.devdunnapps.amplify.utils.TimeUtils
import com.google.accompanist.themeadapter.material3.Mdc3Theme

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

    val songDurationMillis = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    val mediaPosition = viewModel.mediaPosition.collectAsState().value
    val playMode = viewModel.playbackState.collectAsState().value
    val shuffleModel = viewModel.shuffleMode.collectAsState().value
    val repeatMode = viewModel.repeatMode.collectAsState().value

    NowPlayingHeader(
        artworkUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
        title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
        subtitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
        onSeekToPosition = { viewModel.seekTo((it * 1000).toLong()) },
        mediaPosition = mediaPosition,
        songDurationMillis = songDurationMillis,
        playMode = playMode,
        shuffleMode = shuffleModel,
        repeatMode = repeatMode,
        onToggleShuffleClick = viewModel::toggleShuffleState,
        onToggleRepeatClick = viewModel::toggleRepeatState,
        onTogglePlayPause = viewModel::togglePlayingState,
        onSkipPrevious = viewModel::skipToPrevious,
        onSkipNext = viewModel::skipToNext,
        onCollapseNowPlaying = onCollapseNowPlaying,
        onMenuClick = {
            onNowPlayingMenuClick(viewModel.metadata.value.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
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
    playMode: PlaybackStateCompat,
    shuffleMode: Int,
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
                    .padding(horizontal = 26.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artworkUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
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
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                NowPlayingProgressBar(
                    onSeekToPosition = onSeekToPosition,
                    mediaPosition = mediaPosition,
                    songDurationMillis = songDurationMillis
                )

                MediaButtonsRow(
                    playMode = playMode,
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
                contentDescription = null
            )
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null
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
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.offset(y = (-6).dp)
            )

            Text(
                text = TimeUtils.millisecondsToTime(songDurationMillis),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.offset(y = (-6).dp)
            )
        }
    }
}

@Composable
private fun MediaButtonsRow(
    playMode: PlaybackStateCompat,
    shuffleMode: Int,
    repeatMode: Int,
    onToggleShuffleClick: () -> Unit,
    onToggleRepeatClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val shuffleModeIcon = when (shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> Icons.Filled.ShuffleOn
            else -> Icons.Filled.Shuffle
        }
        IconButton(onClick = onToggleShuffleClick) {
            Icon(
                imageVector = shuffleModeIcon,
                contentDescription = null
            )
        }

        IconButton(onClick = onSkipPrevious) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        val playPauseIcon =
            if (playMode.state == PlaybackStateCompat.STATE_PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause
        FloatingActionButton(
            shape = CircleShape,
            onClick = onTogglePlayPause,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = playPauseIcon,
                contentDescription = null
            )
        }

        IconButton(onClick = onSkipNext) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        val repeatModeIcon = when (repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
            PlaybackStateCompat.REPEAT_MODE_ALL -> Icons.Filled.RepeatOn
            else -> Icons.Filled.Repeat
        }
        IconButton(onClick = onToggleRepeatClick) {
            Icon(
                imageVector = repeatModeIcon,
                contentDescription = null
            )
        }
    }
}
