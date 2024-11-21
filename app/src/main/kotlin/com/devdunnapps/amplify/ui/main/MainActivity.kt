package com.devdunnapps.amplify.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import com.devdunnapps.amplify.ui.navigation.MainGraphRoute
import com.devdunnapps.amplify.ui.onboarding.OnboardingRoute
import com.devdunnapps.amplify.ui.theme.Theme
import com.devdunnapps.amplify.utils.PreferencesUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences(PreferencesUtils.PREFERENCES_FILE, MODE_PRIVATE)
        val isUserFirstTime = sharedPref.getBoolean(PreferencesUtils.PREF_USER_FIRST_TIME, true)

        setContent {
            val theme = viewModel.theme.collectAsState().value

            Theme(userSelectedTheme = theme) {
                val playbackState = viewModel.playbackState.collectAsState().value.state
                val currentlyPlayingMetadata = viewModel.mediaMetadata.collectAsState().value

                AmplifyApp(
                    startDestination = if (isUserFirstTime) OnboardingRoute else MainGraphRoute,
                    windowSizeClass = calculateWindowSizeClass(this),
                    playbackState = playbackState,
                    currentlyPlayingMetadata = currentlyPlayingMetadata,
                    onTogglePlayPause = viewModel::togglePlaybackState,
                    onSkipToNext = viewModel::skipToNext
                )
            }
        }
    }
}
