package com.devdunnapps.amplify.ui.main

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import com.devdunnapps.amplify.utils.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {

    var playbackState = musicServiceConnection.playbackState
    var mediaMetadata = musicServiceConnection.nowPlaying

    fun togglePlaybackState() {
        if (playbackState.value.state == PlaybackStateCompat.STATE_PLAYING) {
            musicServiceConnection.transportControls.pause()
        } else {
            musicServiceConnection.transportControls.play()
        }
    }

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()
    }
}
