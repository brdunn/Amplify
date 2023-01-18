package com.devdunnapps.amplify.ui.onboarding

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object WelcomeRoute

@Serializable
data object LoginRoute

@Serializable
data object ServerSelectionRoute

@Serializable
data object LibrarySelectionRoute

fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onFinishOnboarding: () -> Unit
) {
    navigation<OnboardingRoute>(
        startDestination = WelcomeRoute
    ) {
        composable<WelcomeRoute> {
            WelcomeScreen(onNavigateToLogin = { navController.navigate(LoginRoute) })
        }

        composable<LoginRoute> {
            val parentEntry = remember(it) { navController.getBackStackEntry(OnboardingRoute) }
            val viewModel: LoginFlowViewModel = hiltViewModel(parentEntry)
            LoginScreen(
                viewModel = viewModel,
                onNavigateToServerSelection = { navController.navigate(ServerSelectionRoute) }
            )
        }

        composable<ServerSelectionRoute> {
            val navigationGraphEntry = remember(it) {
                navController.getBackStackEntry(OnboardingRoute)
            }
            val viewModel: LoginFlowViewModel = hiltViewModel(navigationGraphEntry)
            ServerSelectionScreen(
                viewModel = viewModel,
                onNavigateToLibrarySelection = { navController.navigate(LibrarySelectionRoute) }
            )
        }

        composable<LibrarySelectionRoute> {
            val navigationGraphEntry = remember(it) {
                navController.getBackStackEntry(OnboardingRoute)
            }
            val viewModel: LoginFlowViewModel = hiltViewModel(navigationGraphEntry)
            LibrarySelectionScreen(
                viewModel = viewModel,
                onFinishOnboarding = onFinishOnboarding
            )
        }
    }
}
