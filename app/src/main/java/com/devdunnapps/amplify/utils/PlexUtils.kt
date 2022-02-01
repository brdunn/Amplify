package com.devdunnapps.amplify.utils

import android.content.Context
import android.content.SharedPreferences

class PlexUtils constructor(context: Context) {

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
        sharedPref.getString(PreferencesUtils.PREF_PLEX_USER_TOKEN, null) ?: "skhqTZcFYbsx3FCieAfq"
    }

    private val serverAddress: String? by lazy {
        sharedPref.getString(PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, null) ?: "https://217-180-231-42.a6f8f71822594078bb98653867eec723.plex.direct:18854"
    }

    fun addKeyAndAddress(url: String?): String {
        return "$serverAddress$url?X-Plex-Token=$userToken"
    }

    fun getSizedImage(url: String, width: Int, height: Int): String {
        return "$serverAddress/photo/:/transcode?width=$width&height=$height&url=$url&X-Plex-Token=$userToken"
    }
}
