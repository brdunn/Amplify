package com.devdunnapps.amplify.ui.playlists

import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.usecases.DeletePlaylistUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistSongsUseCase_Factory
import com.devdunnapps.amplify.domain.usecases.GetPlaylistUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistMenuBottomSheetViewModel @Inject constructor(
    getPlaylistUseCase: GetPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"]!!

    val playlist: LiveData<Resource<Playlist>> = getPlaylistUseCase(playlistId).asLiveData()

    lateinit var isPlaylistDeletionComplete: LiveData<Resource<Playlist>>

    fun deletePlaylist() {
        isPlaylistDeletionComplete = deletePlaylistUseCase(playlistId).asLiveData()
    }
}
