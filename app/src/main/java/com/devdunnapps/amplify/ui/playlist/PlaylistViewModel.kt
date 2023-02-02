package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetPlaylistSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getPlaylistSongsUseCase: GetPlaylistSongsUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playlistId = PlaylistFragmentArgs.fromSavedStateHandle(savedStateHandle).playlistId

    private val _uiState: MutableStateFlow<Resource<PlaylistUIModel>> = MutableStateFlow(Resource.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        gatherPlaylist()
    }

    private fun gatherPlaylist() {
        viewModelScope.launch {
            combine(getPlaylistUseCase(playlistId), getPlaylistSongsUseCase(playlistId)) { playlist, songs ->
                when {
                    playlist is Resource.Error || songs is Resource.Error -> _uiState.emit(Resource.Error())
                    playlist is Resource.Loading || songs is Resource.Loading -> _uiState.emit(Resource.Loading)
                    playlist is Resource.Success && songs is Resource.Success ->
                        _uiState.emit(Resource.Success(PlaylistUIModel(playlist.data, songs.data)))
                }
            }.collect()
        }
    }

    fun refresh() {
        gatherPlaylist()
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }

    fun playPlaylist(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "add_songs_to_queue"
            WhenToPlay.QUEUE -> "play_songs_next"
        }

        val shuffleMode = if (shuffle) PlaybackStateCompat.SHUFFLE_MODE_ALL else PlaybackStateCompat.SHUFFLE_MODE_NONE
        musicServiceConnection.transportControls.setShuffleMode(shuffleMode)

        musicServiceConnection.transportControls.sendCustomAction(action, collectPlaylistBundle())
    }

    private fun collectPlaylistBundle(): Bundle {
        return Bundle().apply {
            val songs = (_uiState.value as? Resource.Success)?.data?.songs ?: return@apply
            putSerializable("songs", songs as Serializable)
        }
    }
}

data class PlaylistUIModel(
    val playlist: Playlist,
    val songs: List<Song>
)
