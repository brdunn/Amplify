package com.devdunnapps.amplify.ui.artist.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.ui.artist.ArtistFragmentArgs
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistAllAlbumsViewModel @Inject constructor(
    application: Application,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val artistId: String = ArtistFragmentArgs.fromSavedStateHandle(savedStateHandle).artistKey

    val artistAlbums: LiveData<Resource<List<Album>>> = getArtistAlbumsUseCase(artistId).asLiveData()
}
