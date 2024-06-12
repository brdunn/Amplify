package com.devdunnapps.amplify

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.devdunnapps.amplify.domain.models.ThemeConfig
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AmplifyApplication : Application() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        // Set the application theme
        applicationScope.launch {
            preferencesRepository.userData.collect {
                when (it.themeConfig) {
                    ThemeConfig.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    ThemeConfig.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ThemeConfig.FOLLOW_SYSTEM ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }
}
