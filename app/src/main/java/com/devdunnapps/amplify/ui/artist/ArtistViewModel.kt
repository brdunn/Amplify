package com.devdunnapps.amplify.ui.artist

import android.app.Application
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSinglesEPsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    application: Application,
    getArtistSongsUseCase: GetArtistSongsUseCase,
    getArtistSinglesEPsUseCase: GetArtistSinglesEPsUseCase,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    getArtistUseCase: GetArtistUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(application) {

    private val artistId: String = savedStateHandle["artistKey"]!!

    private val _artistSongs = MutableStateFlow<Resource<List<Song>>>(Resource.Loading())
    val artistSongs = _artistSongs.asStateFlow()

    private val _artistSinglesEPs = MutableStateFlow<Resource<List<Album>>>(Resource.Loading())
    val artistSinglesEPs = _artistSinglesEPs.asStateFlow()

    private val _artistAlbums = MutableStateFlow<Resource<List<Album>>>(Resource.Loading())
    val artistAlbums = _artistAlbums.asStateFlow()

    private val _artist = MutableStateFlow<Resource<Artist>>(Resource.Loading())
    val artist = _artist.asStateFlow()

    init {
        viewModelScope.launch {
            getArtistSongsUseCase(artistId).collect {
                _artistSongs.emit(it)
            }
        }

        viewModelScope.launch {
            getArtistAlbumsUseCase(artistId).collect {
                _artistAlbums.emit(it)
            }
        }

        viewModelScope.launch {
            getArtistSinglesEPsUseCase(artistId).collect {
                _artistSinglesEPs.emit(it)
            }
        }

        viewModelScope.launch {
            getArtistUseCase(artistId).collect {
                _artist.emit(it)
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }

    fun shuffleArtist() {
        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        musicServiceConnection.transportControls.sendCustomAction("play_songs_now", collectAlbumBundle())
    }

    private fun collectAlbumBundle(): Bundle? {
        val currentValue = _artistSongs.value as? Resource.Success ?: return null
        return Bundle().apply {
            putSerializable("songs", currentValue.data as Serializable)
        }
    }
}
