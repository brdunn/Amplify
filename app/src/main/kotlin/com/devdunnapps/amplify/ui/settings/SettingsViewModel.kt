package com.devdunnapps.amplify.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Preferences
import com.devdunnapps.amplify.domain.models.ThemeConfig
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import com.devdunnapps.amplify.utils.PreferencesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsActivityViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val app: Application
): AndroidViewModel(app) {

    val uiState = preferencesRepository.userData
        .map { SettingsScreenUIState.Content(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsScreenUIState.Loading)

    fun changeTheme(newValue: ThemeConfig) {
        viewModelScope.launch {
            preferencesRepository.setTheme(newValue)
        }
    }

    fun signOut() {
        PreferencesUtils.saveString(app, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY, null)
        PreferencesUtils.saveString(app, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, null)
        PreferencesUtils.saveString(app, PreferencesUtils.PREF_PLEX_TV_USER_TOKEN, null)
        PreferencesUtils.saveString(app, PreferencesUtils.PREF_PLEX_USER_TOKEN, null)
        PreferencesUtils.saveBoolean(app, PreferencesUtils.PREF_USER_FIRST_TIME, true)
    }
}

sealed interface SettingsScreenUIState {
    data class Content(val preferences: Preferences) : SettingsScreenUIState

    object Loading : SettingsScreenUIState
}
