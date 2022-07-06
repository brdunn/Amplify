package com.devdunnapps.amplify.ui.artist

import android.app.Application
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetArtistAlbumsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSinglesEPsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetArtistUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    application: Application,
    getArtistSongsUseCase: GetArtistSongsUseCase,
    getArtistSinglesEPsUseCase: GetArtistSinglesEPsUseCase,
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    getArtistUseCase: GetArtistUseCase,
    savedStateHandle: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(application) {

    private val artistId: String = savedStateHandle["artistKey"]!!

    val artistSongs: LiveData<Resource<List<Song>>> = getArtistSongsUseCase(artistId).asLiveData()

    val artistSinglesEPs = getArtistSinglesEPsUseCase(artistId).asLiveData()

    val artistAlbums: LiveData<Resource<List<Album>>> = getArtistAlbumsUseCase(artistId).asLiveData()

    val artist: LiveData<Resource<Artist>> = getArtistUseCase(artistId).asLiveData()

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }

    fun shuffleArtist() {
        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        musicServiceConnection.transportControls.sendCustomAction("play_songs_now", collectAlbumBundle())
    }

    private fun collectAlbumBundle(): Bundle {
        return Bundle().apply {
            putSerializable("songs", artistSongs.value!!.data as Serializable)
        }
    }
}
