package com.devdunnapps.amplify.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devdunnapps.amplify.ui.navigation.AboutRoute

fun NavGraphBuilder.aboutScreen(onNavigateUp: () -> Unit) {
    composable<AboutRoute> {
        AboutScreen(onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToAbout() {
    navigate(AboutRoute)
}
