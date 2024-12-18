package com.devdunnapps.amplify.ui.artist.songs

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetArtistSongsUseCase
import com.devdunnapps.amplify.ui.navigation.ArtistAllSongsRoute
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAllSongsViewModel @Inject constructor(
    private val getArtistSongsUseCase: GetArtistSongsUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val artistId = savedStateHandle.toRoute<ArtistAllSongsRoute>().artistId

    private val _artistSongs = MutableStateFlow<Resource<List<Song>>>(Resource.Loading)
    val artistSongs = _artistSongs.asStateFlow()

    init {
        getArtistSongs()
    }

    private fun getArtistSongs() {
        viewModelScope.launch {
            val result = getArtistSongsUseCase(artistId)
            if (result is NetworkResponse.Success)
                _artistSongs.emit(Resource.Success(result.data))
            else
                _artistSongs.emit(Resource.Error())
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
