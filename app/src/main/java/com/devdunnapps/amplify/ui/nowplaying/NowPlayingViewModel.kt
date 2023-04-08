package com.devdunnapps.amplify.ui.nowplaying

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.devdunnapps.amplify.utils.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {
    val isPlaying = musicServiceConnection.isPlaying
    val metadata = musicServiceConnection.nowPlaying
    val duration = musicServiceConnection.duration
    val shuffleMode = musicServiceConnection.isShuffleEnabled
    val repeatMode = musicServiceConnection.repeatMode

    val mediaPosition: MutableStateFlow<Long> = MutableStateFlow(0)
    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true

    init {
        checkPlaybackPosition()
    }

    fun togglePlayingState() {
        if (musicServiceConnection.isPlaying.value) {
            musicServiceConnection.pause()
        } else {
            musicServiceConnection.play()
        }
    }

    fun toggleShuffleState() {
        if (musicServiceConnection.isShuffleEnabled.value)
            musicServiceConnection.enableShuffleMode()
        else
            musicServiceConnection.disableShuffleMode()
    }

    fun toggleRepeatState() {
        when (musicServiceConnection.repeatMode.value) {
            Player.REPEAT_MODE_OFF -> musicServiceConnection.setRepeatMode(Player.REPEAT_MODE_ONE)
            Player.REPEAT_MODE_ONE -> musicServiceConnection.setRepeatMode(Player.REPEAT_MODE_ALL)
            Player.REPEAT_MODE_ALL -> musicServiceConnection.setRepeatMode(Player.REPEAT_MODE_OFF)
        }
    }

    fun skipToPrevious() {
        musicServiceConnection.skipToPrevious()
    }

    fun skipToNext() {
        musicServiceConnection.skipToNext()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.seekTo(position)
    }

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = musicServiceConnection.currentPosition
        if (mediaPosition.value != currPosition)
            mediaPosition.value = currPosition
        if (updatePosition)
            checkPlaybackPosition()
    }, 500)

    override fun onCleared() {
        super.onCleared()

        updatePosition = false
    }
}
