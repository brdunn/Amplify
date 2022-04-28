package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetAlbumSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetAlbumUseCase
import com.devdunnapps.amplify.domain.usecases.RateSongUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AlbumMenuBottomSheetViewModel @Inject constructor(
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getAlbumSongsUseCase: GetAlbumSongsUseCase,
    savedStateHandle: SavedStateHandle,
    private val mediaServiceConnection: MusicServiceConnection
): ViewModel() {

    private val albumId: String = savedStateHandle["albumId"]!!

    private val _album = MutableLiveData<Resource<Album>>()
    val album: LiveData<Resource<Album>> = _album

    private var _songs = MutableLiveData<Resource<List<Song>>>()
    val songs: LiveData<Resource<List<Song>>> = _songs

    init {
        gatherAlbum()
        gatherSongs()
    }

    private fun gatherAlbum() {
        viewModelScope.launch {
            getAlbumUseCase(albumId).collect {
                _album.value = it
            }
        }
    }

    private fun gatherSongs() {
        viewModelScope.launch {
            getAlbumSongsUseCase(albumId).collect {
                _songs.value = it
            }
        }
    }

    fun playAlbum(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "play_songs_next"
            WhenToPlay.QUEUE -> "add_songs_to_queue"
        }

        val shuffleMode = if (shuffle) PlaybackStateCompat.SHUFFLE_MODE_ALL else PlaybackStateCompat.SHUFFLE_MODE_NONE
        mediaServiceConnection.transportControls.setShuffleMode(shuffleMode)

        mediaServiceConnection.transportControls.sendCustomAction(action, collectAlbumBundle())
    }

    private fun collectAlbumBundle(): Bundle {
        return Bundle().apply {
            putSerializable("songs", songs.value!!.data as Serializable)
        }
    }
}
