package com.devdunnapps.amplify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devdunnapps.amplify.ui.about.aboutScreen
import com.devdunnapps.amplify.ui.main.mainGraph
import com.devdunnapps.amplify.ui.onboarding.OnboardingDestination
import com.devdunnapps.amplify.ui.onboarding.onboardingGraph
import com.devdunnapps.amplify.ui.settings.settingsScreen

@Composable
internal fun AmplifyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = OnboardingDestination.route,
    onFinishOnboarding: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        onboardingGraph(
            navController = navController,
            onFinishOnboarding = onFinishOnboarding
        )



        settingsScreen(
            onNavigateUp = { navController.popBackStack() }
        )

        aboutScreen(
            onNavigateUp = {  }
        )
    }
}

interface AmplifyNavigationDestination {
    val route: String
    val destination: String
}
