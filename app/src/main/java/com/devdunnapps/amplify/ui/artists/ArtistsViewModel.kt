package com.devdunnapps.amplify.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.usecases.GetArtistsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistsUseCase: GetArtistsUseCase
) : ViewModel() {

    private val _artists = MutableStateFlow<Resource<List<Artist>>>(Resource.Loading())
    val artists: StateFlow<Resource<List<Artist>>> = _artists

    init {
        viewModelScope.launch {
            getArtistsUseCase().collect {
                _artists.value = it
            }
        }
    }
}
