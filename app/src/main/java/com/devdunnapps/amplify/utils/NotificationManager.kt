package com.devdunnapps.amplify.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val NOTIFICATION_LARGE_ICON_SIZE = 144

@UnstableApi
class NotificationManager(
    private val context: Context,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val playerNotificationManager: PlayerNotificationManager = PlayerNotificationManager.Builder(context, 1, "Amplify")
        .setMediaDescriptionAdapter(DescriptionAdapter())
        .setNotificationListener(notificationListener)
        .setChannelNameResourceId(R.string.playback_channel_name)
        .setChannelDescriptionResourceId(R.string.playback_channel_desc)
        .build()
        .apply {
            setUseNextActionInCompactView(true)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
        }

    fun hideNotification() {
        playerNotificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        playerNotificationManager.setPlayer(player)
    }

    @UnstableApi
    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun getCurrentContentTitle(player: Player): String {
            return player.mediaMetadata.title.toString()
        }

        override fun getCurrentContentText(player: Player): String {
            return player.mediaMetadata.artist.toString()
        }

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
            val iconUri = player.mediaMetadata.artworkUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // cache the icon so we don't have to recreate it if we don't need to
                currentIconUri = iconUri

                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriToBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("launchNowPlaying", true)
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
                        or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private suspend fun resolveUriToBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(uri)
                .error(R.drawable.ic_album)
                .size(NOTIFICATION_LARGE_ICON_SIZE)
                .build()
            (ImageLoader(context).execute(request).drawable as BitmapDrawable).bitmap
        }
    }
}
