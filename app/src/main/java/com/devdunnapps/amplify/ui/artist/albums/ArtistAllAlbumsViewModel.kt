package com.devdunnapps.amplify.ui.artist.albums

import android.app.Application
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ArtistAllAlbumsViewModel @Inject constructor(
    application: Application,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val artistId: String = savedStateHandle["artistId"]!!

    val artistAlbums: LiveData<Resource<List<Album>>> = getArtistAlbumsUseCase(artistId).asLiveData()
}
