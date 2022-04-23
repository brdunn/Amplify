package com.devdunnapps.amplify.ui.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.MixedMedia
import com.devdunnapps.amplify.domain.usecases.GetRecentlyPlayedMediaUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recentlyPlayedMediaUseCase: GetRecentlyPlayedMediaUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _recentlyPlayedMedia = MutableLiveData<Resource<MixedMedia>>()
    val recentlyPlayedMedia: LiveData<Resource<MixedMedia>> get() = _recentlyPlayedMedia

    init {
        viewModelScope.launch {
            recentlyPlayedMediaUseCase().collect {
                _recentlyPlayedMedia.value = it
            }
        }
    }

    fun playSong(songIndex: Int) {
        val bundle = Bundle()
        bundle.putSerializable("song", _recentlyPlayedMedia.value!!.data!!.songs[songIndex])
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
