package com.devdunnapps.amplify.ui.nowplaying

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lyrics
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    val hasLyrics = viewModel.hasLyrics.collectAsState().value

    // Only display content if something is playing
    if (metadata == NOTHING_PLAYING)
        return

    val songDurationMillis = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    val mediaPosition = viewModel.mediaPosition.collectAsState().value
    val playMode = viewModel.playbackState.collectAsState().value
    val shuffleModel = viewModel.shuffleMode.collectAsState().value
    val repeatMode = viewModel.repeatMode.collectAsState().value

    NowPlaying(
        hasLyrics = hasLyrics,
        artworkUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
        songId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
        title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
        subtitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
        onSeekToPosition = { viewModel.seekTo((it * 1000).toLong()) },
        onFinishSeekToPosition = viewModel::finishSeek,
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
            onNowPlayingMenuClick(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
        }
    )
}

@Composable
private fun NowPlaying(
    hasLyrics: Boolean,
    artworkUrl: String,
    songId: String,
    title: String,
    subtitle: String,
    onSeekToPosition: (Float) -> Unit,
    onFinishSeekToPosition: () -> Unit,
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
                .systemBarsPadding()
        ) {
            NowPlayingTopBar(
                hasLyrics = hasLyrics,
                songId = songId,
                title = title,
                subtitle = subtitle,
                onCollapseNowPlaying = onCollapseNowPlaying,
                onMenuClick = onMenuClick
            )

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
                        .whenTrue(getCurrentSizeClass() != WindowWidthSizeClass.Compact) {
                            widthIn(
                                max = 350.dp
                            )
                        }
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
                    onFinishSeekToPosition = onFinishSeekToPosition,
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
private fun NowPlayingTopBar(
    hasLyrics: Boolean,
    songId: String,
    title: String,
    subtitle: String,
    onCollapseNowPlaying: () -> Unit,
    onMenuClick: () -> Unit
) {
    var isLyricsBottomSheetVisible by rememberSaveable { mutableStateOf(false) }

    if (isLyricsBottomSheetVisible) {
        LyricsBottomSheet(
            songId = songId,
            title = title,
            subtitle = subtitle,
            onDismiss = { isLyricsBottomSheetVisible = false }
        )
    }

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (hasLyrics) {
                IconButton(onClick = { isLyricsBottomSheetVisible = true }) {
                    Icon(
                        imageVector = Icons.Filled.Lyrics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
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
}

@Composable
private fun NowPlayingProgressBar(
    onSeekToPosition: (Float) -> Unit,
    onFinishSeekToPosition: () -> Unit,
    mediaPosition: Long,
    songDurationMillis: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        var sliderPosition by remember(mediaPosition) { mutableFloatStateOf((mediaPosition / 1000).toFloat()) }

        Slider(
            value = sliderPosition,
            valueRange = 0f..(songDurationMillis / 1000).toFloat(),
            onValueChange = {
                sliderPosition = it
                onSeekToPosition(it)
            },
            onValueChangeFinished = onFinishSeekToPosition,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primaryContainer,
                activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
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
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = TimeUtils.millisecondsToTime(songDurationMillis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
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
    val rowSpacing = if (getCurrentSizeClass() == WindowWidthSizeClass.Compact) 8.dp else 16.dp
    Row(
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
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
                tint = MaterialTheme.colorScheme.onBackground,
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
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
