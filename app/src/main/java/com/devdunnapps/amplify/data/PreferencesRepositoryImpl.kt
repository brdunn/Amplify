package com.devdunnapps.amplify.data

import androidx.datastore.core.DataStore
import com.devdunnapps.amplify.ThemeConfigProto
import com.devdunnapps.amplify.UserPreferences
import com.devdunnapps.amplify.copy
import com.devdunnapps.amplify.domain.models.Preferences
import com.devdunnapps.amplify.domain.models.ThemeConfig
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor (
    private val preferences: DataStore<UserPreferences>
): PreferencesRepository {

    override val userData = preferences.data
        .map {
            Preferences(
                themeConfig = when (it.themeConfig) {
                    ThemeConfigProto.DARK_THEME_CONFIG_DARK -> ThemeConfig.DARK
                    ThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> ThemeConfig.LIGHT
                    else -> ThemeConfig.FOLLOW_SYSTEM
                }
            )
        }

    override suspend fun setTheme(themeConfig: ThemeConfig) {
        preferences.updateData {
            it.copy {
                this.themeConfig = when (themeConfig) {
                    ThemeConfig.FOLLOW_SYSTEM -> ThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    ThemeConfig.LIGHT -> ThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    ThemeConfig.DARK -> ThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }
}
