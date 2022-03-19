package com.devdunnapps.amplify.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.devdunnapps.amplify.utils.PreferencesUtils

class SettingsFragmentViewModel(private val app: Application): AndroidViewModel(app) {

    fun signOut() {
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY, null)
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, null)
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_USER_TOKEN, null)
        PreferencesUtils.saveBoolean(app.applicationContext, PreferencesUtils.PREF_USER_FIRST_TIME, true)
    }
}
