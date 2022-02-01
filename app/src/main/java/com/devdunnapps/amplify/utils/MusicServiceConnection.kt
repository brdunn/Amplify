package com.devdunnapps.amplify.utils

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.devdunnapps.amplify.utils.MusicServiceConnection.MediaBrowserConnectionCallback

/**
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance, typically a
 * [MusicService] or one of its subclasses.
 *
 * Typically it's best to construct/inject dependencies either using DI or, as UAMP does,
 * using [InjectorUtils] in the app module. There are a few difficulties for that here:
 * - [MediaBrowserCompat] is a final class, so mocking it directly is difficult.
 * - A [MediaBrowserConnectionCallback] is a parameter into the construction of
 *   a [MediaBrowserCompat], and provides callbacks to this class.
 * - [MediaBrowserCompat.ConnectionCallback.onConnected] is the best place to construct
 *   a [MediaControllerCompat] that will be used to control the [MediaSessionCompat].
 *
 *  Because of these reasons, rather than constructing additional classes, this is treated as
 *  a black box (which is why there's very little logic here).
 *
 *  This is also why the parameters to construct a [MusicServiceConnection] are simple
 *  parameters, rather than private properties. They're only required to build the
 *  [MediaBrowserConnectionCallback] and [MediaBrowserCompat] objects.
 */
class MusicServiceConnection(context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableLiveData(false)

    val playbackState = MutableLiveData(EMPTY_PLAYBACK_STATE)

    val nowPlaying = MutableLiveData(NOTHING_PLAYING)

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    val shuffleMode: Int
        get() = mediaController.shuffleMode

    val repeatMode: Int
        get() = mediaController.repeatMode

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    private lateinit var mediaController: MediaControllerCompat

    private inner class MediaBrowserConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // When ExoPlayer stops we will receive a callback with "empty" metadata. This is a
            // metadata object which has been instantiated with default values. The default value
            // for media ID is null so we assume that if this value is null we are not playing
            // anything.
            nowPlaying.postValue(
                if (metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == null) {
                    NOTHING_PLAYING
                } else {
                    metadata
                }
            )
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) = Unit

        override fun onSessionEvent(event: String?, extras: Bundle?) = Unit

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events
         * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
         * send it on to the other callback.
         */
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()
