package com.devdunnapps.amplify.ui.nowplaying

import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalTextStyle
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.devdunnapps.amplify.utils.TimeUtils
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel(),
    onCollapseNowPlaying: () -> Unit,
    onNowPlayingMenuClick: (String) -> Unit
) {
    Mdc3Theme {
        val metadata = viewModel.metadata.collectAsState().value

        // Only display content if something is playing
        if (metadata == NOTHING_PLAYING)
            return@Mdc3Theme

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
    val themeOnBackground = MaterialTheme.colorScheme.onBackground
    val themeBackground = MaterialTheme.colorScheme.background
    val themeAccent = MaterialTheme.colorScheme.primary
    var colorOnBackground by remember { mutableStateOf(themeOnBackground) }
    var colorBackground by remember { mutableStateOf(themeBackground) }
    var colorAccent by remember { mutableStateOf(themeAccent) }

    CompositionLocalProvider(
        LocalContentColor provides colorOnBackground,
        LocalTextStyle provides LocalTextStyle.current.copy(color = colorOnBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = animateColorAsState(targetValue = colorBackground).value)
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
                val artworkPainter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artworkUrl)
                        .allowHardware(false)
                        .size(Size.ORIGINAL)
                        .build()
                )

                val painterState = artworkPainter.state
                if (painterState is AsyncImagePainter.State.Success) {
                    LaunchedEffect(painterState) {
                        val palette = Palette
                            .from((painterState.result.drawable as BitmapDrawable).bitmap)
                            .generate()

                        colorAccent = Color(palette.getVibrantColor(android.graphics.Color.BLACK))

                        palette.darkMutedSwatch?.let { darkMutedSwatch ->
                            colorBackground = Color(darkMutedSwatch.rgb)
                            colorOnBackground = Color(darkMutedSwatch.bodyTextColor)
                        }
                    }
                }

                Image(
                    painter = artworkPainter,
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
                    onSkipNext = onSkipNext,
                    colorAccent = colorAccent
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
    colorAccent: Color,
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
            containerColor = colorAccent,
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
