package com.devdunnapps.amplify.domain.repository

import com.devdunnapps.amplify.domain.models.Preferences
import com.devdunnapps.amplify.domain.models.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    val userData: Flow<Preferences>

    suspend fun setTheme(themeConfig: ThemeConfig)
}
