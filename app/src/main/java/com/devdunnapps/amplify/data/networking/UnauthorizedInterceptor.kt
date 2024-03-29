package com.devdunnapps.amplify.data.networking

import android.content.Context
import android.content.Intent
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.PreferencesUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection

/**
 * Clears the user's data and navigates the user to the login flow when an API returns a 401.
 */
class UnauthorizedInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Ignore API calls that allow for unauthenticated requests. This header is internal to the app.
            if (response.request.header("X-Amplify-Ignore-Auth-Errors") == "1") {
                return response
            }

            // The use must have revoked their token, navigate back to the login flow
            PreferencesUtils.saveString(context, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY, null)
            PreferencesUtils.saveString(context, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, null)
            PreferencesUtils.saveString(context, PreferencesUtils.PREF_PLEX_TV_USER_TOKEN, null)
            PreferencesUtils.saveString(context, PreferencesUtils.PREF_PLEX_USER_TOKEN, null)
            PreferencesUtils.saveBoolean(context, PreferencesUtils.PREF_USER_FIRST_TIME, true)

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
        return response
    }
}
