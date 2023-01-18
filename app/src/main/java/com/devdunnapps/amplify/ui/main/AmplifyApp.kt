package com.devdunnapps.amplify.ui.main

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.ui.navigation.TopLevelDestination
import com.devdunnapps.amplify.ui.nowplaying.NowPlayingScreen
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AmplifyApp(
    windowSizeClass: WindowSizeClass,
    appState: AmplifyAppState = rememberAmplifyAppState(windowSizeClass = windowSizeClass),
    playbackState: Int,
    currentlyPlayingMetadata: MediaMetadataCompat
) {
    ModalBottomSheetLayout(bottomSheetNavigator = appState.bottomSheetNavigator) {
        Scaffold(
            bottomBar = {
                AmplifyBottomBar(
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentTopLevelDestination,
                )
            },
            topBar = {
                RootDestinationAppBar(
                    title = "Amplify",
                    onNavigateToSearch = { /*TODO*/ },
                    onNavigateToSettings = { /*TODO*/ },
                    onNavigateToAbout = { /*TODO*/ }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            BottomSheetScaffold(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
                scaffoldState = appState.bottomSheetScaffoldState,
                sheetPeekHeight = 64.dp,

                sheetContent = {
                    val bottomSheetState = appState.bottomSheetScaffoldState.bottomSheetState
                    val coroutineScope = rememberCoroutineScope()

                    println(bottomSheetState.currentValue)

                    if (bottomSheetState.targetValue == SheetValue.Expanded || bottomSheetState.isVisible) {
                        NowPlayingScreen(
                            onCollapseNowPlaying = {
                                coroutineScope.launch { bottomSheetState.hide() }
                            },
                            onNowPlayingMenuClick = {
                            }
                        )
                    }

//                    if (bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
                        if (currentlyPlayingMetadata != NOTHING_PLAYING) {
                            NowPlayingCollapsed(
                                albumArtUrl = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                                title = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                                subtitle = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
                                isPlaying = playbackState == PlaybackStateCompat.STATE_PLAYING,
                                onPlayPauseClick = { },
                                onSkipClick = { },
                                modifier = Modifier.clickable {
                                    coroutineScope.launch { bottomSheetState.expand() }
                                }
                            )
                        }
//                    }
                }
            ) { childPadding ->
                Column(modifier = Modifier.padding(childPadding)) {
                    NavHost(
                        navController = appState.navController,
                        startDestination = "main_route"
                    ) {
                        mainGraph(
                            navController = appState.navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AmplifyBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: TopLevelDestination,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val isSelected = currentDestination == destination

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
