package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetAlbumSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetAlbumUseCase
import com.devdunnapps.amplify.ui.navigation.AlbumRoute
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.TimeUtils
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlbumUseCase: GetAlbumUseCase,
    getAlbumSongsUseCase: GetAlbumSongsUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val albumId = savedStateHandle.toRoute<AlbumRoute>().albumId

    private val _album = MutableStateFlow<Resource<AlbumScreenUIModel>>(Resource.Loading)
    val album = _album.asStateFlow()

    init {
        viewModelScope.launch {
            val albumDeferred = async { getAlbumUseCase(albumId) }
            val albumSongsDeferred = async { getAlbumSongsUseCase(albumId) }

            val albumResult = albumDeferred.await()
            val albumSongsResult = albumSongsDeferred.await()

            when {
                albumResult is NetworkResponse.Failure -> _album.emit(Resource.Error())
                albumSongsResult is NetworkResponse.Failure -> _album.emit(Resource.Error())
                albumResult is NetworkResponse.Success && albumSongsResult is NetworkResponse.Success -> {
                    var duration = 0L
                    albumSongsResult.data.forEach { duration += it.duration }

                    _album.emit(
                        Resource.Success(
                            AlbumScreenUIModel(
                                album = albumResult.data,
                                songs = albumSongsResult.data,
                                duration = TimeUtils.millisecondsToMinutes(duration)
                            )
                        )
                    )
                }
            }
        }
    }

    fun playSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable("song", song)
        musicServiceConnection.transportControls.sendCustomAction("play_song", bundle)
    }

    fun playAlbum(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val action = when (whenToPlay) {
            WhenToPlay.NOW -> "play_songs_now"
            WhenToPlay.NEXT -> "play_songs_next"
            WhenToPlay.QUEUE -> "add_songs_to_queue"
        }

        val shuffleMode = if (shuffle) PlaybackStateCompat.SHUFFLE_MODE_ALL else PlaybackStateCompat.SHUFFLE_MODE_NONE
        musicServiceConnection.transportControls.setShuffleMode(shuffleMode)

        musicServiceConnection.transportControls.sendCustomAction(action, collectAlbumBundle())
    }

    private fun collectAlbumBundle(): Bundle {
        return Bundle().apply {
            val albumContent = _album.value as? Resource.Success<AlbumScreenUIModel> ?: return@apply
            putSerializable("songs", albumContent.data.songs as Serializable)
        }
    }
}
