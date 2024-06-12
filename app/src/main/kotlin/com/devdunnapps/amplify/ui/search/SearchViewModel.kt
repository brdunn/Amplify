package com.devdunnapps.amplify.ui.search

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.SearchLibraryUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_TIME = 100L

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

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        val trimmedText = query.trim()
        if (trimmedText.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_TIME)

                searchLibraryUseCase(trimmedText).collect {
                    _searchResults.value = it
                }
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
