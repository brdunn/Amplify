package com.devdunnapps.amplify.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.settingsScreen(onNavigateUp: () -> Unit) {
    composable(
        route = "settings"
    ) {
        SettingsRoute(
            onNavigateUp = onNavigateUp,
            onSignOutClick = {}
        )
    }
}

fun NavController.navigateToSettings() {
    navigate("settings")
}
