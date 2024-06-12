package com.devdunnapps.amplify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devdunnapps.amplify.ui.onboarding.OnboardingDestination
import com.devdunnapps.amplify.ui.onboarding.onboardingGraph

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
    }
}

interface AmplifyNavigationDestination {
    val route: String
    val destination: String
}
