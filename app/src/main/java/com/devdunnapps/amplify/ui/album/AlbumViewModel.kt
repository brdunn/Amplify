package com.devdunnapps.amplify.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetAlbumSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetAlbumUseCase
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.TimeUtils
import com.devdunnapps.amplify.utils.WhenToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlbumUseCase: GetAlbumUseCase,
    getAlbumSongsUseCase: GetAlbumSongsUseCase,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val albumId = AlbumFragmentArgs.fromSavedStateHandle(savedStateHandle).albumId

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
        musicServiceConnection.playSong(song)
    }

    fun playAlbum(whenToPlay: WhenToPlay = WhenToPlay.NOW, shuffle: Boolean = false) {
        val songs = (_album.value as? Resource.Success)?.data?.songs ?: return

        if (shuffle)
            musicServiceConnection.enableShuffleMode()
        else
            musicServiceConnection.disableShuffleMode()

        when (whenToPlay) {
            WhenToPlay.NOW -> musicServiceConnection.playSongs(songs)
            WhenToPlay.NEXT -> musicServiceConnection.playSongsNext(songs)
            WhenToPlay.QUEUE -> musicServiceConnection.addSongsToQueue(songs)
        }
    }
}
