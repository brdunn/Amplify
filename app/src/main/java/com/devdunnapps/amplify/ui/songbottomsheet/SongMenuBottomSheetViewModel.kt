package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Rating
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetSongUseCase
import com.devdunnapps.amplify.domain.usecases.RateSongUseCase
import com.devdunnapps.amplify.domain.usecases.RemoveSongFromPlaylistUseCase
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
    private val removeSongFromPlaylistUseCase: RemoveSongFromPlaylistUseCase,
    savedStateHandle: SavedStateHandle,
    private val mediaServiceConnection: MusicServiceConnection
): ViewModel() {

    private val args = SongMenuBottomSheetFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val songId = args.songId
    private val playlistId = args.playlistId

    private val _screenState = MutableStateFlow<Resource<SongBottomSheetState>>(Resource.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        fetchSong()
    }

    fun rateSong(rating: Int) {
        val currentState = (screenState.value as? Resource.Success)?.data ?: return

        viewModelScope.launch {
            val newRating = if (currentState.song.userRating == rating) Rating.THUMB_GONE else rating
            _screenState.emit(
                Resource.Success(currentState.copy(song = currentState.song.copy(userRating = newRating)))
            )

            if (rateSongUseCase(songId, newRating) is NetworkResponse.Failure) {
                _screenState.emit(Resource.Success(currentState))
            }
        }
    }

    private fun fetchSong() {
        viewModelScope.launch {
            val song = getSongUseCase(songId)
            if (song is NetworkResponse.Success) {
                _screenState.emit(
                    Resource.Success(SongBottomSheetState(song = song.data, refreshPreviousScreen = false))
                )
            }
        }
    }

    fun playSong(whenToPlay: WhenToPlay = WhenToPlay.NOW) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "add_songs_to_queue"
            WhenToPlay.QUEUE -> "play_songs_next"
        }

        val curState = screenState.value as? Resource.Success ?: return
        val songs = ArrayList<Serializable>()
        songs.add(curState.data.song)
        val bundle = Bundle()
        bundle.putSerializable("songs", songs)
        mediaServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        mediaServiceConnection.transportControls.sendCustomAction(action, bundle)
    }

    fun removeSongFromPlaylist() {
        playlistId?.let { playlistId ->
            viewModelScope.launch {
                if (removeSongFromPlaylistUseCase(songId, playlistId)) {
                    val curData = (screenState.value as? Resource.Success ?: return@launch).data
                    _screenState.emit(Resource.Success(curData.copy(refreshPreviousScreen = true)))
                }
            }
        }
    }
}

data class SongBottomSheetState(
    val song: Song,
    val refreshPreviousScreen: Boolean
)
