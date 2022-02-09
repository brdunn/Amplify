package com.devdunnapps.amplify.ui.artist.songs

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetArtistSongsUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistAllSongsViewModel @Inject constructor(
    application: Application,
    getArtistSongsUseCase: GetArtistSongsUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(application) {

    private val artistId: String = savedStateHandle["artistId"]!!

    val artistSongs: LiveData<Resource<List<Song>>> = getArtistSongsUseCase(artistId).asLiveData()

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }
}
