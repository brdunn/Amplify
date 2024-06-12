package com.devdunnapps.amplify.ui.playlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
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
            val playlist = getPlaylistUseCase(playlistId)
            if (playlist is NetworkResponse.Success)
                _playlist.emit(Resource.Success(playlist.data))
            else
                _playlist.emit(Resource.Error())
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            if (deletePlaylistUseCase(playlistId) is NetworkResponse.Success) {
                _closeObservable.emit(Unit)
            }
        }
    }
}
