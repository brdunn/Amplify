package com.devdunnapps.amplify.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devdunnapps.amplify.ui.navigation.SettingsRoute

fun NavGraphBuilder.settingsScreen(onNavigateUp: () -> Unit) {
    composable<SettingsRoute> {
        SettingsRoute(onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToSettings() {
    navigate(SettingsRoute)
}
