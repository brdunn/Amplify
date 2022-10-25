package com.devdunnapps.amplify.ui.search

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.SearchLibraryUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchLibraryUseCase: SearchLibraryUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _searchResults: MutableStateFlow<Resource<SearchResults>> = MutableStateFlow(
        Resource.Success(
            SearchResults(
                songs = emptyList(),
                albums = emptyList(),
                artists = emptyList(),
                playlists = emptyList()
            )
        )
    )
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            searchLibraryUseCase(query).collect {
                _searchResults.value = it
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
