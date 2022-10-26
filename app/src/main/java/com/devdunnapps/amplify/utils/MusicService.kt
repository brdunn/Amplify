package com.devdunnapps.amplify.utils

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    // We must implement our own inject because Hilt does not support MediaBrowserServiceCompat
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MusicServiceEntryPoint {
        fun repository(): PlexRepository
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    private var currentSongID: String? = null
    private var isForegroundService = false

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    private val playerListener = PlayerEventListener()

    private val appAudioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setHandleAudioBecomingNoisy(true)
            setAudioAttributes(appAudioAttributes, true)
            addListener(playerListener)
        }
    }

    private val mediaSessionCallbacks = object : MediaSessionCompat.Callback() {

        override fun onCustomAction(action: String, extras: Bundle) {
            super.onCustomAction(action, extras)
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            when (action) {
                "play_song" -> {
                    exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                    val song = extras.getSerializable("song") as Song
                    val mediaItem = convertSongToMediaItem(song)
                    if ((mediaItem.localConfiguration?.tag as Song).id != currentSongID) {
                        playMediaItem(mediaItem)
                    } else {
                        play() // this song is already playing so don't restart
                    }
                }
                "play_songs_now" -> {
                    playMediaItems(bundleToMediaItems(extras))
                }
                "add_songs_to_queue" -> {
                    if (exoPlayer.mediaItemCount == 0) {
                        playMediaItems(bundleToMediaItems(extras))
                    } else {
                        exoPlayer.addMediaItems(bundleToMediaItems(extras))
                    }
                }
                "play_songs_next" -> {
                    if (exoPlayer.mediaItemCount == 0) {
                        playMediaItems(bundleToMediaItems(extras))
                    } else {
                        exoPlayer.apply {
                            addMediaItems(currentMediaItemIndex + 1, bundleToMediaItems(extras))
                        }
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun bundleToMediaItems(bundle: Bundle): ArrayList<MediaItem> {
            val songs: ArrayList<Song> = bundle.getSerializable("songs") as ArrayList<Song>
            val mediaItems = arrayListOf<MediaItem>()
            for (song in songs) {
                mediaItems.add(convertSongToMediaItem(song))
            }
            return mediaItems
        }

        override fun onPause() {
            super.onPause()
            pause()
        }

        override fun onPlay() {
            super.onPlay()
            play()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            exoPlayer.seekToNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            exoPlayer.seekToPrevious()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            exoPlayer.seekTo(pos)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            exoPlayer.repeatMode = repeatMode
            mediaSession.setRepeatMode(repeatMode)
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            exoPlayer.shuffleModeEnabled = shuffleMode != PlaybackStateCompat.SHUFFLE_MODE_NONE
            mediaSession.setShuffleMode(shuffleMode)
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(exoPlayer)
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) {
                            // If playback is paused we remove the foreground state which allows the
                            // notification to be dismissed. An alternative would be to provide a
                            // "close" button in the notification which stops playback and clears
                            // the notification.
                            stopForeground(false)
                            isForegroundService = false
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            } else {
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            currentSongID = (mediaItem?.localConfiguration?.tag as Song).id

            // TODO: only mark an item as played if the entire song has been played
            val hiltEntryPoint =
                EntryPointAccessors.fromApplication(applicationContext, MusicServiceEntryPoint::class.java)
            serviceScope.launch {
                hiltEntryPoint.repository().markSongAsListened(currentSongID!!).collect()
            }

            mediaSession.setMetadata(convertSongToMetadata(mediaItem.localConfiguration?.tag as Song))
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }
    }

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this, MusicService::class.java.simpleName).apply {
            setCallback(mediaSessionCallbacks)
            isActive = true
            setSessionToken(sessionToken)
        }

        notificationManager = NotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )
    }

    private fun playMediaItem(mediaItem: MediaItem) {
        exoPlayer.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    private fun playMediaItems(mediaItems: List<MediaItem>) {
        exoPlayer.apply {
            setMediaItems(mediaItems, true)
            prepare()
            play()
        }
    }

    private fun play() {
        exoPlayer.apply {
            playWhenReady = true
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            mediaSession.isActive = true
        }
    }

    private fun pause() {
        exoPlayer.apply {
            playWhenReady = false
            if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }

    /**
     * Called when the app is swiped away from the recent apps menu
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        job.cancel()

        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    private fun updatePlaybackState(state: Int) {
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, exoPlayer.currentPosition, 1F)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build()
        )
    }

    private fun convertSongToMediaItem(song: Song): MediaItem {
        val songUrl = PlexUtils.getInstance(applicationContext).addKeyAndAddress(song.songUrl)
        return MediaItem.Builder()
            .setUri(songUrl)
            .setTag(song)
            .build()
    }

    private fun convertSongToMetadata(song: Song): MediaMetadataCompat {
        val artworkUrl = PlexUtils.getInstance(applicationContext).addKeyAndAddress(song.thumb)
        return Builder()
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_ALBUM, song.albumName)
                .putString(METADATA_KEY_ARTIST, song.artistName)
                .putString(METADATA_KEY_ALBUM_ART_URI, artworkUrl)
                .putString(METADATA_KEY_ART_URI, artworkUrl)
                .putLong(METADATA_KEY_DURATION, song.duration)
                .putString(METADATA_KEY_MEDIA_ID, song.id)
                .putString("ALBUM_ID", song.albumId)
                .putString("ARTIST_ID", song.artistId)
                .build()
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {}

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = BrowserRoot("", null)

    private inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {

        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }
}
