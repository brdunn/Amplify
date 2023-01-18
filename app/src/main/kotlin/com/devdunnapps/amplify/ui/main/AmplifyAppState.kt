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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.navigation.AlbumsRoute
import com.devdunnapps.amplify.ui.navigation.ArtistsRoute
import com.devdunnapps.amplify.ui.navigation.HomeRoute
import com.devdunnapps.amplify.ui.navigation.PlaylistsRoute
import com.devdunnapps.amplify.ui.navigation.SongsRoute
import com.devdunnapps.amplify.ui.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAmplifyAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
): AmplifyAppState =
    remember(navController, windowSizeClass, bottomSheetScaffoldState) {
        AmplifyAppState(navController, windowSizeClass, bottomSheetScaffoldState)
    }

@OptIn( ExperimentalMaterial3Api::class)
@Stable
class AmplifyAppState(
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass,
    val bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val topLevelDestinations = listOf(
        TopLevelDestination(
            route = HomeRoute,
            selectedIcon = R.drawable.ic_home,
            unselectedIcon = R.drawable.ic_home_outlined,
            iconText = R.string.home
        ),
        TopLevelDestination(
            route = ArtistsRoute,
            selectedIcon = R.drawable.ic_artists,
            unselectedIcon = R.drawable.ic_artists_outlined,
            iconText = R.string.artists
        ),
        TopLevelDestination(
            route = AlbumsRoute,
            selectedIcon = R.drawable.ic_album,
            unselectedIcon = R.drawable.ic_album_outlined,
            iconText = R.string.albums
        ),
        TopLevelDestination(
            route = SongsRoute,
            selectedIcon = R.drawable.ic_songs,
            unselectedIcon = R.drawable.ic_songs_outlined,
            iconText = R.string.songs
        ),
        TopLevelDestination(
            route = PlaylistsRoute,
            selectedIcon = R.drawable.ic_playlists,
            unselectedIcon = R.drawable.ic_playlists_outlined,
            iconText = R.string.playlists
        )
    )

    var currentTopLevelDestination by mutableStateOf(topLevelDestinations[0])
//        private set
}
