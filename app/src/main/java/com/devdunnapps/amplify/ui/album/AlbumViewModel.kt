package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetAlbumSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetAlbumUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlbumUseCase: GetAlbumUseCase,
    getAlbumSongsUseCase: GetAlbumSongsUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val albumId: String = savedStateHandle["albumId"]!!

    val album: LiveData<Resource<Album>> = getAlbumUseCase(albumId).asLiveData()

    private var _songs = MutableLiveData<Resource<List<Song>>>()
    val songs: LiveData<Resource<List<Song>>> = _songs

    private var _albumDuration = MutableLiveData<Long>()
    val albumDuration: LiveData<Long> = _albumDuration

    init {
        viewModelScope.launch {
            getAlbumSongsUseCase(albumId).collect {
                if (it is Resource.Success) {
                    var duration = 0L
                    for (song in it.data!!) {
                        duration += song.duration
                    }
                    _albumDuration.value = duration
                }

                _songs.value = it
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }

    fun playAlbum(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "add_songs_to_queue"
            WhenToPlay.QUEUE -> "play_songs_next"
        }

        val shuffleMode = if (shuffle) PlaybackStateCompat.SHUFFLE_MODE_ALL else PlaybackStateCompat.SHUFFLE_MODE_NONE
        musicServiceConnection.transportControls.setShuffleMode(shuffleMode)

        musicServiceConnection.transportControls.sendCustomAction(action, collectAlbumBundle())
    }

    private fun collectAlbumBundle(): Bundle {
        return Bundle().apply {
            putSerializable("songs", songs.value!!.data as Serializable)
        }
    }
}
