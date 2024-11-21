package com.devdunnapps.amplify.ui.main

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import com.devdunnapps.amplify.utils.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val preferencesRepository: PreferencesRepository
): ViewModel() {

    var playbackState = musicServiceConnection.playbackState
    var mediaMetadata = musicServiceConnection.nowPlaying

    val theme = preferencesRepository.userData
        .map { it.themeConfig }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            // This is not ideal but we do not want to make assumptions about which theme to use as
            // it could cause a flash of bright colors on the screen.
            runBlocking { preferencesRepository.userData.first().themeConfig }
        )

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
