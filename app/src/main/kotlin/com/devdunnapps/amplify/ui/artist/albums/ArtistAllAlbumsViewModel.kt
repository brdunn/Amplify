package com.devdunnapps.amplify.ui.artist.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSinglesEPsUseCase
import com.devdunnapps.amplify.ui.navigation.ArtistAllAlbumsRoute
import com.devdunnapps.amplify.ui.navigation.ArtistRoute
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAllAlbumsViewModel @Inject constructor(
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    getArtistSinglesEPsUseCase: GetArtistSinglesEPsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs = savedStateHandle.toRoute<ArtistAllAlbumsRoute>()
    private val artistId = navArgs.artistId

    private val _artistAlbums = MutableStateFlow<Resource<List<Album>>>(Resource.Loading)
    val artistAlbums = _artistAlbums.asStateFlow()

    init {
        viewModelScope.launch {
            if (navArgs.isSinglesEPs) {
                val result = getArtistSinglesEPsUseCase(artistId)
                if (result is NetworkResponse.Success)
                    _artistAlbums.emit(Resource.Success(result.data))
                else
                    _artistAlbums.emit(Resource.Error())
            } else {
                val result = getArtistAlbumsUseCase(artistId)
                if (result is NetworkResponse.Success)
                    _artistAlbums.emit(Resource.Success(result.data))
                else
                    _artistAlbums.emit(Resource.Error())
            }
        }
    }
}
