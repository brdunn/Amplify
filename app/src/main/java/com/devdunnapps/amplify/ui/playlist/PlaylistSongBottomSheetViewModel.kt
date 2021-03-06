package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.RemoveSongFromPlaylistUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class PlaylistSongBottomSheetViewModel @Inject constructor(
    private val removeSongFromPlaylistUseCase: RemoveSongFromPlaylistUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val mediaServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"]!!
    private val song: Song = savedStateHandle["song"]!!

    lateinit var isSongRemovedComplete: LiveData<Resource<Playlist>>

    fun removeSongFromPlaylist() {
        isSongRemovedComplete = removeSongFromPlaylistUseCase(song.id, playlistId).asLiveData()
    }

    fun playSong(whenToPlay: WhenToPlay = WhenToPlay.NOW) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "add_songs_to_queue"
            WhenToPlay.QUEUE -> "play_songs_next"
        }

        val songs = ArrayList<Serializable>()
        songs.add(song)
        val bundle = Bundle()
        bundle.putSerializable("songs", songs)
        mediaServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        mediaServiceConnection.transportControls.sendCustomAction(action, bundle)
    }
}
