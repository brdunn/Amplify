package com.devdunnapps.amplify.data.networking

import android.content.Context
import com.devdunnapps.amplify.utils.PreferencesUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the user's authentication token as a header to the request.
 */
class AuthorizationInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_USER_TOKEN).orEmpty()
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Plex-Token", token)
            .build()
        return chain.proceed(request)
    }
}
