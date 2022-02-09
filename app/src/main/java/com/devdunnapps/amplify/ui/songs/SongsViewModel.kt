package com.devdunnapps.amplify.ui.songs

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetSongsUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    getSongsUseCase: GetSongsUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    val songs: LiveData<Resource<List<Song>>> = getSongsUseCase().asLiveData()

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
