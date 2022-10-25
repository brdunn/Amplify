package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
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

    private val playlistId: String = savedStateHandle["playlistId"]!!

    private val _playlist = MutableLiveData<Resource<Playlist>>()
    val playlist: LiveData<Resource<Playlist>> = _playlist

    private val _playlistSongs: MutableStateFlow<Resource<List<Song>>> = MutableStateFlow(Resource.Loading())
    val playlistSongs = _playlistSongs.asStateFlow()

    init {
        gatherPlaylist()
        gatherSongs()
    }

    private fun gatherPlaylist() {
        viewModelScope.launch {
            getPlaylistUseCase(playlistId).collect {
                _playlist.value = it
            }
        }
    }

    fun gatherSongs() {
        viewModelScope.launch {
            getPlaylistSongsUseCase(playlistId).collect {
                _playlistSongs.value = it
            }
        }
    }

    fun playSong(songIndex: Int) {
        val bundle = Bundle()
        bundle.putSerializable("song", playlistSongs.value!!.data!![songIndex])
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
            putSerializable("songs", playlistSongs.value!!.data as Serializable)
        }
    }
}
