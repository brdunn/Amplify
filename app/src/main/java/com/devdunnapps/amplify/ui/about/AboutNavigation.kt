package com.devdunnapps.amplify.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.aboutScreen(onNavigateUp: () -> Unit) {
    composable(route = "about") {
        AboutScreen(onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToAbout() {
    navigate("about")
}
