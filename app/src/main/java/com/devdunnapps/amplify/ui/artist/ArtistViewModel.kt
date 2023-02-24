package com.devdunnapps.amplify.ui.artist

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
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
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    getArtistSongsUseCase: GetArtistSongsUseCase,
    getArtistSinglesEPsUseCase: GetArtistSinglesEPsUseCase,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    getArtistUseCase: GetArtistUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val artistId = ArtistFragmentArgs.fromSavedStateHandle(savedStateHandle).artistKey

    private val _artistSongs = MutableStateFlow<Resource<List<Song>>>(Resource.Loading)
    val artistSongs = _artistSongs.asStateFlow()

    private val _artistSinglesEPs = MutableStateFlow<Resource<List<Album>>>(Resource.Loading)
    val artistSinglesEPs = _artistSinglesEPs.asStateFlow()

    private val _artistAlbums = MutableStateFlow<Resource<List<Album>>>(Resource.Loading)
    val artistAlbums = _artistAlbums.asStateFlow()

    private val _artist = MutableStateFlow<Resource<Artist>>(Resource.Loading)
    val artist = _artist.asStateFlow()

    init {
        viewModelScope.launch {
            val result = getArtistSongsUseCase(artistId)
            if (result is NetworkResponse.Success)
                _artistSongs.emit(Resource.Success(result.data))
            else
                _artistSongs.emit(Resource.Error())
        }

        viewModelScope.launch {
            val result = getArtistAlbumsUseCase(artistId)
            if (result is NetworkResponse.Success)
                _artistAlbums.emit(Resource.Success(result.data))
            else
                _artistAlbums.emit(Resource.Error())
        }

        viewModelScope.launch {
            val artistSinglesEPs = getArtistSinglesEPsUseCase(artistId)

            if (artistSinglesEPs is NetworkResponse.Success)
                _artistSinglesEPs.emit(Resource.Success(artistSinglesEPs.data))
            else
                _artistSinglesEPs.emit(Resource.Error())
        }

        viewModelScope.launch {
            val artist = getArtistUseCase(artistId)

            if (artist is NetworkResponse.Failure)
                _artist.emit(Resource.Error())
            else
                _artist.emit(Resource.Success(artist.data))
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
