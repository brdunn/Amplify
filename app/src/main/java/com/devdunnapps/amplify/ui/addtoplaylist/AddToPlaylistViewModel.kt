package com.devdunnapps.amplify.ui.addtoplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val songId: String = savedStateHandle["songId"]!!

    private val _playlists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading())
    val playlists = _playlists.asStateFlow()

    private val _closeObservable = MutableSharedFlow<Unit>()
    val closeObservable = _closeObservable.asSharedFlow()

    init {
        gatherPlaylists()
    }

    private fun gatherPlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect {
                _playlists.value = it
            }
        }
    }

    fun addSongToPlaylist(playlistId: String) {
        viewModelScope.launch {
            addSongToPlaylistUseCase(songId, playlistId).collect {
                when (it) {
                    is Resource.Success -> _closeObservable.emit(Unit)
                    else -> Unit
                }
            }
        }
    }
}
