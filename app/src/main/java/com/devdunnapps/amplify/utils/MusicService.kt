package com.devdunnapps.amplify.utils

import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON = "com.devdunnapps.amplify.utils.SHUFFLE_ON"
private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF = "com.devdunnapps.amplify.utils.SHUFFLE_OFF"

@UnstableApi
@AndroidEntryPoint
class MusicService : MediaSessionService() {

    // We must implement our own inject because Hilt does not support MediaBrowserServiceCompat
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MusicServiceEntryPoint {
        fun repository(): PlexRepository
    }

    private var mediaSession: MediaSession? = null
//    private lateinit var notificationManager: NotificationManager
//    private var currentSongID: String? = null
    private var isForegroundService = false

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    private val appAudioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(appAudioAttributes, true)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .build()

//        notificationManager = NotificationManager(this, PlayerNotificationListener())
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

//    override fun onUpdateNotification(session: MediaSession) {
//    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON -> exoPlayer.shuffleModeEnabled = true
                CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF -> exoPlayer.shuffleModeEnabled = false
                else -> return Futures.immediateFuture(SessionResult(SessionResult.RESULT_ERROR_BAD_VALUE))
            }

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems = mediaItems.map { it.buildUpon().setUri(it.requestMetadata.mediaUri).build() }
            return Futures.immediateFuture(updatedMediaItems)
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
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }

        job.cancel()

        super.onDestroy()
    }

//    private inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
//
//        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
//            if (ongoing && !isForegroundService) {
//                ContextCompat.startForegroundService(
//                    applicationContext,
//                    Intent(applicationContext, this@MusicService.javaClass)
//                )
//                startForeground(notificationId, notification)
//                isForegroundService = true
//            }
//        }
//
//        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
//            stopForeground(true)
//            isForegroundService = false
//            stopSelf()
//        }
//    }
}
