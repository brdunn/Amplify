package com.devdunnapps.amplify.ui.nowplaying

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.usecases.GetSongLyricsUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.currentPlayBackPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val getSongLyricsUseCase: GetSongLyricsUseCase
): ViewModel() {

    val playbackState = musicServiceConnection.playbackState
    val metadata = musicServiceConnection.nowPlaying

    val shuffleMode: MutableStateFlow<Int> = MutableStateFlow(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    val repeatMode: MutableStateFlow<Int> = MutableStateFlow(PlaybackStateCompat.REPEAT_MODE_NONE)

    private val _hasLyrics = MutableStateFlow(false)
    val hasLyrics = _hasLyrics.asStateFlow()

    private val _mediaPosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    val mediaPosition = _mediaPosition.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true

    init {
        checkPlaybackPosition()

        viewModelScope.launch {
            metadata.collect {
                checkForSongLyrics()
            }
        }
    }

    fun togglePlayingState() {
        if (playbackState.value.state == PlaybackStateCompat.STATE_PLAYING) {
            musicServiceConnection.transportControls.pause()
        } else {
            musicServiceConnection.transportControls.play()
        }
    }

    fun toggleShuffleState() {
        if (musicServiceConnection.shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
            shuffleMode.value = PlaybackStateCompat.SHUFFLE_MODE_ALL
        } else {
            musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
            shuffleMode.value = PlaybackStateCompat.SHUFFLE_MODE_NONE
        }
    }

    fun toggleRepeatState() {
        when (musicServiceConnection.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                repeatMode.value = PlaybackStateCompat.REPEAT_MODE_ONE
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                repeatMode.value = PlaybackStateCompat.REPEAT_MODE_ALL
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
                repeatMode.value = PlaybackStateCompat.REPEAT_MODE_NONE
            }
        }
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun seekTo(position: Long) {
        updatePosition = false
        _mediaPosition.value = position
    }

    fun finishSeek() {
        musicServiceConnection.transportControls.seekTo(mediaPosition.value)
        updatePosition = true
    }

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.value.currentPlayBackPosition
        if (mediaPosition.value != currPosition)
            _mediaPosition.value = currPosition
        if (updatePosition)
            checkPlaybackPosition()
    }, 500)

    override fun onCleared() {
        super.onCleared()

        updatePosition = false
    }

    private fun checkForSongLyrics() {
        viewModelScope.launch {
            val songId = metadata.value.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            getSongLyricsUseCase(songId).collect {
                if (it is Resource.Success) {
                    _hasLyrics.emit(true)
                }
            }
        }
    }
}
