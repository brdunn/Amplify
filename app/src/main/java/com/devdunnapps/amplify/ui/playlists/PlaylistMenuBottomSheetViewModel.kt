package com.devdunnapps.amplify.ui.playlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.usecases.DeletePlaylistUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistMenuBottomSheetViewModel @Inject constructor(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"]!!

    private val _playlist = MutableStateFlow<Resource<Playlist>>(Resource.Loading)
    val playlist = _playlist.asStateFlow()

    private val _closeObservable = MutableSharedFlow<Unit>()
    val closeObservable = _closeObservable.asSharedFlow()

    init {
        getPlaylist()
    }

    private fun getPlaylist() {
        viewModelScope.launch {
            getPlaylistUseCase(playlistId).collect {
                _playlist.emit(it)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            deletePlaylistUseCase(playlistId).collect {
                if (it is Resource.Success)
                    _closeObservable.emit(Unit)
            }
        }
    }
}
