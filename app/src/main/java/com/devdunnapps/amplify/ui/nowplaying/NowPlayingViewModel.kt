package com.devdunnapps.amplify.ui.nowplaying

import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.currentPlayBackPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {

    val playbackState = musicServiceConnection.playbackState
    val metadata = musicServiceConnection.nowPlaying

    val shuffleMode: MutableLiveData<Int> = MutableLiveData(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    val repeatMode: MutableLiveData<Int> = MutableLiveData(PlaybackStateCompat.REPEAT_MODE_NONE)

    val mediaPosition:MutableLiveData<Long> = MutableLiveData(0)
    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true

    init {
        checkPlaybackPosition()
    }

    fun togglePlayingState() {
        if (playbackState.value!!.state == PlaybackStateCompat.STATE_PLAYING) {
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
        musicServiceConnection.transportControls.seekTo(position)
    }

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.value!!.currentPlayBackPosition
        if (mediaPosition.value != currPosition)
            mediaPosition.postValue(currPosition)
        if (updatePosition)
            checkPlaybackPosition()
    }, 500)

    override fun onCleared() {
        super.onCleared()

        updatePosition = false
    }
}
