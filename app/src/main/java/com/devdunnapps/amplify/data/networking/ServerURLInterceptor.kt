package com.devdunnapps.amplify.data.networking

import android.content.Context
import com.devdunnapps.amplify.utils.PreferencesUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Sets the base url of the request to the server that the user selected during sign in.
 */
class ServerURLInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val serverURL = PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS).orEmpty()
        val request = chain.request()
        val oldBaseUrl = "${request.url.scheme}://${request.url.host}"
        val newRequest = request
            .newBuilder()
            .url(request.url.toString().replace(oldBaseUrl, serverURL))
            .build()
        return chain.proceed(newRequest)
    }
}
