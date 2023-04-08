package com.devdunnapps.amplify.ui.main

import androidx.lifecycle.ViewModel
import com.devdunnapps.amplify.utils.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {
    val isPlaying = musicServiceConnection.isPlaying
    val playbackState = musicServiceConnection.playbackState
    val mediaMetadata = musicServiceConnection.nowPlaying

    fun togglePlaybackState() {
        if (musicServiceConnection.isPlaying.value) {
            musicServiceConnection.pause()
        } else {
            musicServiceConnection.play()
        }
    }

    fun skipToNext() {
        musicServiceConnection.skipToNext()
    }
}
