package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetSongUseCase
import com.devdunnapps.amplify.domain.usecases.RateSongUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class SongMenuBottomSheetViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val rateSongUseCase: RateSongUseCase,
    savedStateHandle: SavedStateHandle,
    private val mediaServiceConnection: MusicServiceConnection
): ViewModel() {

    private val songId = SongMenuBottomSheetFragmentArgs.fromSavedStateHandle(savedStateHandle).songId

    private val _song = MutableStateFlow<Resource<Song>>(Resource.Loading())
    val song = _song.asStateFlow()

    init {
        fetchSong()
    }

    fun rateSong(rating: Int) {
        viewModelScope.launch {
            rateSongUseCase(songId, rating).collect {
                if (it is Resource.Success) {
                    fetchSong()
                }
            }
        }
    }

    private fun fetchSong() {
        viewModelScope.launch {
            getSongUseCase(songId).collect {
                _song.value = it
            }
        }
    }

    fun playSong(whenToPlay: WhenToPlay = WhenToPlay.NOW) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "add_songs_to_queue"
            WhenToPlay.QUEUE -> "play_songs_next"
        }

        val songs = ArrayList<Serializable>()
        songs.add(song.value.data!!)
        val bundle = Bundle()
        bundle.putSerializable("songs", songs)
        mediaServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        mediaServiceConnection.transportControls.sendCustomAction(action, bundle)
    }
}
