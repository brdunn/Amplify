package com.devdunnapps.amplify.utils

import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.devdunnapps.amplify.domain.models.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicServiceConnection(private val context: Context, serviceComponent: ComponentName) {
    private val mediaControllerFuture: ListenableFuture<MediaController>

    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE.state)
    val playbackState = _playbackState.asStateFlow()

    private val _nowPlaying = MutableStateFlow(NOTHING_PLAYING)
    val nowPlaying = _nowPlaying.asStateFlow()

    val currentPosition: Long
        get() = mediaController?.currentPosition ?: 0

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode = _repeatMode.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    fun enableShuffleMode() {
        mediaController?.shuffleModeEnabled = true
    }

    fun disableShuffleMode() {
        mediaController?.shuffleModeEnabled = false
    }

    fun setRepeatMode(repeatMode: Int) {
        mediaController?.repeatMode = repeatMode
    }

    fun playSong(song: Song) {
        mediaController?.apply {
            setMediaItem(convertSongToMediaItem(song))
            prepare()
            play()
        }
    }

    fun playSongNext(song: Song) {
        mediaController?.apply {
            addMediaItem(1, convertSongToMediaItem(song))
            prepare()
            play()
        }
    }

    fun addSongToQueue(song: Song) {
        mediaController?.apply {
            addMediaItem(mediaItemCount, convertSongToMediaItem(song))
            prepare()
            play()
        }
    }

    fun playSongs(songs: List<Song>) {
        mediaController?.apply {
            setMediaItems(songs.map { convertSongToMediaItem(it) })
            prepare()
            play()
        }
    }

    fun playSongsNext(songs: List<Song>) {
        mediaController?.apply {
            addMediaItems(1, songs.map { convertSongToMediaItem(it) })
            prepare()
            play()
        }
    }

    fun addSongsToQueue(songs: List<Song>) {
        mediaController?.apply {
            addMediaItems(mediaItemCount, songs.map { convertSongToMediaItem(it) })
            prepare()
            play()
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    private fun convertSongToMediaItem(song: Song): MediaItem {
        val songUrl = PlexUtils.getInstance(context).addKeyAndAddress(song.songUrl)
        return MediaItem.Builder()
            .setRequestMetadata(RequestMetadata.Builder().setMediaUri(songUrl.toUri()).build())
            .setMediaMetadata(convertSongToMetadata(song))
            .build()
    }

    private fun convertSongToMetadata(song: Song): MediaMetadata {
        val artworkUrl = PlexUtils.getInstance(context).addKeyAndAddress(song.thumb)
        return MediaMetadata.Builder()
            .setTitle(song.title)
            .setAlbumTitle(song.albumName)
            .setArtist(song.artistName)
            .setArtworkUri(artworkUrl.toUri())
            .build()
    }

    init {
        val sessionToken = SessionToken(context, serviceComponent)
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener(
            {
                mediaController?.addListener(object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onMediaMetadataChanged(mediaMetadata)
                        _nowPlaying.value = mediaMetadata
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        _playbackState.value = playbackState
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        _isPlaying.value = isPlaying
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        _isShuffleEnabled.value = shuffleModeEnabled
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                        super.onRepeatModeChanged(repeatMode)
                        _repeatMode.value = repeatMode
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        // TODO: doesn't work
                        if (playbackState == Player.STATE_READY)
                            _duration.value = mediaController?.duration ?: 0
                    }
                })
            },
            MoreExecutors.directExecutor()
        )
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackState = PlaybackState.Builder()
    .setState(PlaybackState.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadata = MediaMetadata.EMPTY
