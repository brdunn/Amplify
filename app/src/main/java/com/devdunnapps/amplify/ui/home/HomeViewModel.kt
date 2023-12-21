package com.devdunnapps.amplify.ui.home

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plexRepository: PlexRepository,
    private val plexTVRepository: PlexTVRepository,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _uiState: MutableStateFlow<Resource<HomeUIModel>> =
        MutableStateFlow(Resource.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userDeferred = async { plexTVRepository.getUser() }
            val recentArtistsDeferred = async { plexRepository.getRecentlyPlayedArtists() }
            val recentlyAddedAlbumsDeferred = async { plexRepository.getRecentlyAddedAlbums() }
            val recentSongsDeferred = async { plexRepository.getRecentlyPlayedSongs() }

            val user = userDeferred.await()
            val recentArtists = recentArtistsDeferred.await()
            val recentlyAddedAlbums = recentlyAddedAlbumsDeferred.await()
            val recentSongs = recentSongsDeferred.await()

            if (
                user is NetworkResponse.Success &&
                recentArtists is NetworkResponse.Success &&
                recentlyAddedAlbums is NetworkResponse.Success &&
                recentSongs is NetworkResponse.Success
            ) {
                val uiModel = HomeUIModel(
                    user.data.avatar,
                    user.data.displayName ?: user.data.username,
                    recentArtists.data,
                    recentlyAddedAlbums.data,
                    recentSongs.data
                )
                _uiState.emit(Resource.Success(uiModel))
            } else {
                _uiState.emit(Resource.Error())
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}

data class HomeUIModel(
    val userAvatar: String,
    val userTitle: String,
    val recentArtists: List<Artist>,
    val recentlyAdded: List<Album>,
    val recentSongs: List<Song>
)
