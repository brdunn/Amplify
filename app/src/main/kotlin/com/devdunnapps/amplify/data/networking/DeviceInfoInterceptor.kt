package com.devdunnapps.amplify.data.networking

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.devdunnapps.amplify.BuildConfig
import com.devdunnapps.amplify.R
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds basic information about the client device to the request's headers such as the Android version and application
 * version.
 */
class DeviceInfoInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("X-Plex-Product", context.getString(R.string.app_name))
            .addHeader("X-Plex-Version", BuildConfig.VERSION_NAME)
            .addHeader("X-Plex-Platform", "Android")
            .addHeader("X-Plex-Platform-Version", Build.VERSION.SDK_INT.toString())
            .addHeader("X-Plex-Device", Build.MODEL)
            .addHeader("X-Plex-Device-Name", Settings.Global.getString(context.contentResolver, "device_name"))
            .addHeader("X-Plex-Client-Identifier", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
            .build()
        return chain.proceed(request)
    }
}
