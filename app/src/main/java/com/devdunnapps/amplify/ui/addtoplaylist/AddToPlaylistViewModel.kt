package com.devdunnapps.amplify.ui.addtoplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.usecases.AddSongToPlaylistUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    getPlaylistsUseCase: GetPlaylistsUseCase,
    val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: String = savedStateHandle["songId"]!!

    val playlists: LiveData<Resource<List<Playlist>>> = getPlaylistsUseCase().asLiveData()

    lateinit var isSongAddedComplete: LiveData<Resource<Playlist>>

    fun addSongToPlaylist(playlistId: String) {
        isSongAddedComplete = addSongToPlaylistUseCase(songId, playlistId).asLiveData()
    }
}
