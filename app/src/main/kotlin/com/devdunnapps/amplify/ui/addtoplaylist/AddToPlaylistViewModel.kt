package com.devdunnapps.amplify.ui.addtoplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.usecases.AddSongToPlaylistUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    val getPlaylistsUseCase: GetPlaylistsUseCase,
    val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId = savedStateHandle.get<String>("songId")!!

    private val _playlists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading)
    val playlists = _playlists.asStateFlow()

    private val _closeObservable = MutableSharedFlow<Unit>()
    val closeObservable = _closeObservable.asSharedFlow()

    init {
        gatherPlaylists()
    }

    private fun gatherPlaylists() {
        viewModelScope.launch {
            val result = getPlaylistsUseCase()
            if (result is NetworkResponse.Success)
                _playlists.emit(Resource.Success(result.data))
            else
                _playlists.emit(Resource.Error())
        }
    }

    fun addSongToPlaylist(playlistId: String) {
        viewModelScope.launch {
            if (addSongToPlaylistUseCase(songId, playlistId)) {
                _closeObservable.emit(Unit)
            }
        }
    }
}
