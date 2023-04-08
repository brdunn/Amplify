package com.devdunnapps.amplify.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.usecases.GetPlaylistSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetPlaylistUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getPlaylistSongsUseCase: GetPlaylistSongsUseCase,
    private val plexRepository: PlexRepository,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playlistId = PlaylistFragmentArgs.fromSavedStateHandle(savedStateHandle).playlistId

    private val _uiState: MutableStateFlow<Resource<PlaylistUIModel>> = MutableStateFlow(Resource.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        gatherPlaylist()
    }

    private fun gatherPlaylist() {
        viewModelScope.launch {
            val playlistDeferred = async { getPlaylistUseCase(playlistId) }
            val playlistSongsDeferred = async { getPlaylistSongsUseCase(playlistId) }

            val playlistResult = playlistDeferred.await()
            val playlistSongsResult = playlistSongsDeferred.await()

            when {
                playlistResult is NetworkResponse.Failure || playlistSongsResult is NetworkResponse.Failure ->
                    _uiState.emit(Resource.Error())

                playlistResult is NetworkResponse.Success && playlistSongsResult is NetworkResponse.Success ->
                    _uiState.emit(Resource.Success(PlaylistUIModel(playlistResult.data, playlistSongsResult.data)))
            }
        }
    }

    fun refresh() {
        gatherPlaylist()
    }

    fun playSong(song: Song) {
        musicServiceConnection.playSong(song)
    }

    fun playPlaylist(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val songs = (_uiState.value as? Resource.Success)?.data?.songs ?: return

        if (shuffle)
            musicServiceConnection.enableShuffleMode()
        else
            musicServiceConnection.disableShuffleMode()

        when (whenToPlay) {
            WhenToPlay.NOW -> musicServiceConnection.playSongs(songs)
            WhenToPlay.NEXT -> musicServiceConnection.playSongsNext(songs)
            WhenToPlay.QUEUE -> musicServiceConnection.addSongsToQueue(songs)
        }
    }

    fun editPlaylistMetadata(title: String, summary: String) = viewModelScope.launch {
        val currentScreenState = _uiState.value
        val currentScreenData = (currentScreenState as? Resource.Success)?.data ?: return@launch

        if (currentScreenData.playlist.title == title && currentScreenData.playlist.summary == summary)
            return@launch

        val request = plexRepository.editPlaylistMetadata(playlistId = playlistId, title = title, summary = summary)
        if (request is NetworkResponse.Success) {
            gatherPlaylist()
        } else {
            _uiState.emit(currentScreenState)
        }
    }
}

data class PlaylistUIModel(
    val playlist: Playlist,
    val songs: List<Song>
)
