package com.devdunnapps.amplify.ui.onboarding

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devdunnapps.amplify.ui.navigation.AmplifyNavigationDestination

object OnboardingDestination : AmplifyNavigationDestination {
    override val route = "onboarding_route"
    override val destination = "onboarding_destination"
}

object WelcomeDestination : AmplifyNavigationDestination {
    override val route = "welcome_route"
    override val destination = "welcome_destination"
}

object LoginDestination : AmplifyNavigationDestination {
    override val route = "login_route"
    override val destination = "login_destination"
}

object ServerSelectionDestination : AmplifyNavigationDestination {
    override val route = "server_selection_route"
    override val destination = "server_selection_destination"
}

object LibrarySelectionDestination : AmplifyNavigationDestination {
    override val route = "library_selection_route"
    override val destination = "library_selection_destination"
}

fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onFinishOnboarding: () -> Unit
) {
    navigation(
        route = OnboardingDestination.route,
        startDestination = WelcomeDestination.route
    ) {
        composable(route = WelcomeDestination.route) {
            WelcomeScreen(onNavigateToLogin = { navController.navigate(LoginDestination.route) })
        }

        composable(route = LoginDestination.route) {
            val parentEntry = remember(it) { navController.getBackStackEntry(OnboardingDestination.route) }
            val viewModel: LoginFlowViewModel = hiltViewModel(parentEntry)
            LoginScreen(
                viewModel = viewModel,
                onNavigateToServerSelection = { navController.navigate(ServerSelectionDestination.route) }
            )
        }

        composable(route = ServerSelectionDestination.route) {
            val navigationGraphEntry = remember(it) { navController.getBackStackEntry(OnboardingDestination.route) }
            val viewModel: LoginFlowViewModel = hiltViewModel(navigationGraphEntry)
            ServerSelectionScreen(
                viewModel = viewModel,
                onNavigateToLibrarySelection = { navController.navigate(LibrarySelectionDestination.route) }
            )
        }

        composable(route = LibrarySelectionDestination.route) {
            val navigationGraphEntry = remember(it) { navController.getBackStackEntry(OnboardingDestination.route) }
            val viewModel: LoginFlowViewModel = hiltViewModel(navigationGraphEntry)
            LibrarySelectionScreen(
                viewModel = viewModel,
                onFinishOnboarding = onFinishOnboarding
            )
        }
    }
}
