package com.devdunnapps.amplify.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.usecases.CreatePlaylistUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase
) : ViewModel() {

    private val _playlists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading)
    val playlists = _playlists.asStateFlow()

    init {
        gatherPlaylists()
    }

    fun createPlaylist(playlistTitle: String) {
        viewModelScope.launch {
           if (createPlaylistUseCase(playlistTitle) is NetworkResponse.Success) {
               gatherPlaylists()
           }
        }
    }

    fun gatherPlaylists() {
        viewModelScope.launch {
            val result = getPlaylistsUseCase()
            if (result is NetworkResponse.Success)
                _playlists.emit(Resource.Success(result.data))
            else
                _playlists.emit(Resource.Error())
        }
    }
}
