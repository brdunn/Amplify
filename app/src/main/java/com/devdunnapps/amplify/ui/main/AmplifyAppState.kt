package com.devdunnapps.amplify.ui.main

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.devdunnapps.amplify.ui.navigation.TopLevelDestination
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun rememberAmplifyAppState(
    windowSizeClass: WindowSizeClass,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
): AmplifyAppState =
    remember(navController, bottomSheetNavigator, windowSizeClass, bottomSheetScaffoldState) {
        AmplifyAppState(navController, bottomSheetNavigator, windowSizeClass, bottomSheetScaffoldState)
    }

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3Api::class)
@Stable
class AmplifyAppState(
    val navController: NavHostController,
    val bottomSheetNavigator: BottomSheetNavigator,
    val windowSizeClass: WindowSizeClass,
    val bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    var currentTopLevelDestination: TopLevelDestination by mutableStateOf(TopLevelDestination.ARTISTS)
        private set

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        currentTopLevelDestination = topLevelDestination

        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.ARTISTS -> navController.navigate(ArtistsDestination.route)
            TopLevelDestination.ALBUMS -> navController.navigate(AlbumsDestination.route)
            TopLevelDestination.SONGS -> navController.navigate(SongsDestination.route)
            TopLevelDestination.PLAYLISTS -> navController.navigate(PlaylistsDestination.route)
        }
    }
}
