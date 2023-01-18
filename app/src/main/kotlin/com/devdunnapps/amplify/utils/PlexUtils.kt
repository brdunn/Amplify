package com.devdunnapps.amplify.utils

import android.content.Context
import android.content.SharedPreferences

class PlexUtils(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: PlexUtils? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlexUtils(context).also {
                    INSTANCE = it
                }
            }
    }

    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(PreferencesUtils.PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    private val userToken: String? by lazy {
        sharedPref.getString(PreferencesUtils.PREF_PLEX_USER_TOKEN, null)
    }

    private val serverAddress: String? by lazy {
        sharedPref.getString(PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, null)
    }

    fun addKeyAndAddress(url: String?): String {
        return "$serverAddress$url?X-Plex-Token=$userToken"
    }

    fun getSizedImage(url: String, width: Int = 400, height: Int = 400): String {
        return "$serverAddress/photo/:/transcode?width=$width&height=$height&url=$url&X-Plex-Token=$userToken"
    }
}
