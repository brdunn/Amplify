package com.devdunnapps.amplify.ui.artist.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSinglesEPsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAllAlbumsViewModel @Inject constructor(
    application: Application,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    getArtistSinglesEPsUseCase: GetArtistSinglesEPsUseCase,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val artistId: String = savedStateHandle["artistId"]!!

    private val _artistAlbums = MutableStateFlow<Resource<List<Album>>>(Resource.Loading())
    val artistAlbums = _artistAlbums.asStateFlow()

    init {
        viewModelScope.launch {
            val shouldLoadSinglesEPs: Boolean = savedStateHandle["isSinglesEPs"] ?: false
            if (shouldLoadSinglesEPs) {
                getArtistSinglesEPsUseCase(artistId).collect {
                    _artistAlbums.emit(it)
                }
            } else {
                getArtistAlbumsUseCase(artistId).collect {
                    _artistAlbums.emit(it)
                }
            }
        }
    }
}
