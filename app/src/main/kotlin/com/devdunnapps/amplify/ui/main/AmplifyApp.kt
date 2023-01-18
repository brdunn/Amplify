package com.devdunnapps.amplify.ui.main

import android.annotation.SuppressLint
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.about.aboutScreen
import com.devdunnapps.amplify.ui.about.navigateToAbout
import com.devdunnapps.amplify.ui.components.NowPlayingCollapsed
import com.devdunnapps.amplify.ui.navigation.HomeRoute
import com.devdunnapps.amplify.ui.navigation.MainGraphRoute
import com.devdunnapps.amplify.ui.navigation.TopLevelDestination
import com.devdunnapps.amplify.ui.nowplaying.navigateToNowPlaying
import com.devdunnapps.amplify.ui.nowplaying.nowPlayingScreen
import com.devdunnapps.amplify.ui.onboarding.LibrarySelectionRoute
import com.devdunnapps.amplify.ui.onboarding.onboardingGraph
import com.devdunnapps.amplify.ui.search.navigateToSearch
import com.devdunnapps.amplify.ui.settings.navigateToSettings
import com.devdunnapps.amplify.ui.settings.settingsScreen
import com.devdunnapps.amplify.ui.songbottomsheet.openSongSongBottomSheet
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.stefanoq21.material3.navigation.ModalBottomSheetLayout
import com.stefanoq21.material3.navigation.rememberBottomSheetNavigator

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmplifyApp(
    startDestination: Any,
    windowSizeClass: WindowSizeClass,
    appState: AmplifyAppState = rememberAmplifyAppState(windowSizeClass = windowSizeClass),
    playbackState: Int,
    currentlyPlayingMetadata: MediaMetadataCompat,
    onTogglePlayPause: () -> Unit,
    onSkipToNext: () -> Unit
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val mainNavController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
        NavHost(
            navController = appState.navController,
            startDestination = startDestination
        ) {
            onboardingGraph(
                navController = appState.navController,
                onFinishOnboarding = {
                    appState.navController.navigate(MainGraphRoute) {
                        popUpTo<LibrarySelectionRoute> {
                            inclusive = true
                        }
                    }
                }
            )

            composable<MainGraphRoute> {
                val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

                // Ugly hack to ensure home is re-selected when navigating through the back stack
                LaunchedEffect(navBackStackEntry) {
                    if (navBackStackEntry?.destination?.hasRoute(HomeRoute::class) == true) {
                        appState.currentTopLevelDestination = appState.topLevelDestinations[0]
                    }
                }

                val topBarActions: @Composable RowScope.() -> Unit = {
                    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

                    IconButton(onClick = { mainNavController.navigateToSearch() }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                    }

                    IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.settings)) },
                            onClick = {
                                appState.navController.navigateToSettings()
                                isMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.about)) },
                            onClick = {
                                appState.navController.navigateToAbout()
                                isMenuExpanded = false
                            }
                        )
                    }
                }


                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                Scaffold(
                    bottomBar = {
                        Column {
                            if (currentlyPlayingMetadata != NOTHING_PLAYING) {
                                NowPlayingCollapsed(
                                    albumArtUrl = currentlyPlayingMetadata.getString(
                                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
                                    ),
                                    title = currentlyPlayingMetadata.getString(
                                        MediaMetadataCompat.METADATA_KEY_TITLE
                                    ),
                                    subtitle = currentlyPlayingMetadata.getString(
                                        MediaMetadataCompat.METADATA_KEY_ARTIST
                                    ),
                                    isPlaying = playbackState == PlaybackStateCompat.STATE_PLAYING,
                                    onPlayPauseClick = onTogglePlayPause,
                                    onSkipClick = onSkipToNext,
                                    modifier = Modifier.clickable {
                                        appState.navController.navigateToNowPlaying()
                                    }
                                )
                            }

                            AmplifyBottomBar(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = { topLevelDestination ->
                                    appState.currentTopLevelDestination = topLevelDestination

                                    mainNavController.navigate(topLevelDestination.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(mainNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }

                                        // Avoid multiple copies of the same destination when
                                        // re-selecting the same item
                                        launchSingleTop = true
                                        // Restore state when re-selecting a previously selected item
                                        restoreState = true
                                    }
                                },
                                isSelected = { topLevelDestination ->
                                    topLevelDestination == appState.currentTopLevelDestination
                                },
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets(0.dp),
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxSize()
                ) { padding ->
                    MainNavGraph(
                        navController = mainNavController,
                        modifier = Modifier
                            .padding(padding)
                            .consumeWindowInsets(padding),
                        topBarActions = topBarActions
                    )
                }
            }

            nowPlayingScreen(
                onCollapseNowPlaying = { appState.navController.popBackStack() },
                onNowPlayingMenuClick = { songId -> mainNavController.openSongSongBottomSheet(songId) }
            )

            settingsScreen(onNavigateUp = { appState.navController.popBackStack() })

            aboutScreen(onNavigateUp = { appState.navController.popBackStack() })
        }
    }
}

@Composable
private fun AmplifyBottomBar(
    destinations: List<TopLevelDestination<out Any>>,
    onNavigateToDestination: (TopLevelDestination<out Any>) -> Unit,
    isSelected: (TopLevelDestination<out Any>) -> Boolean,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val isSelected = isSelected(destination)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (isSelected)
                                destination.selectedIcon
                            else
                                destination.unselectedIcon
                        ),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = destination.iconText)) }
            )
        }
    }
}
