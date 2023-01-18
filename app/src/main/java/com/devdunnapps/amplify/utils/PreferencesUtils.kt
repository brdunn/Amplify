package com.devdunnapps.amplify.utils

import android.content.Context

object PreferencesUtils {

    const val PREFERENCES_FILE = "amplify_settings"
    const val PREF_USER_FIRST_TIME = "user_first_time"
    const val PREF_PLEX_SERVER_ADDRESS = "user_plex_server_ip_address"
    const val PREF_PLEX_USER_TOKEN = "user_plex_token"
    const val PREF_PLEX_SERVER_LIBRARY = "user_plex_server_library"
    const val PREF_THEME = "theme"

    fun readSharedSetting(ctx: Context, settingName: String): String? {
        val sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(settingName, null)
    }

    fun saveString(ctx: Context, settingName: String, settingValue: String?) {
        val sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(settingName, settingValue)
        editor.apply()
    }

    fun saveBoolean(ctx: Context, settingName: String, settingValue: Boolean) {
        val sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(settingName, settingValue)
        editor.apply()
    }
}
