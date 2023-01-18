package com.devdunnapps.amplify.ui.nowplaying

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.devdunnapps.amplify.ui.navigation.NowPlayingRoute

fun NavGraphBuilder.nowPlayingScreen(
    onCollapseNowPlaying: () -> Unit,
    onNowPlayingMenuClick: (String) -> Unit
) {
    composable<NowPlayingRoute>(
        deepLinks = listOf(
            navDeepLink<NowPlayingRoute>(basePath = "amplify://now-playing")
        ),
        enterTransition = {
            slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { 4 * it }
            )
        },
        exitTransition = {
            slideOutVertically(
                animationSpec = tween(1000),
                targetOffsetY = { 4 * it }
            )
        }
    ) {
        NowPlayingScreen(
            onCollapseNowPlaying = onCollapseNowPlaying,
            onNowPlayingMenuClick = onNowPlayingMenuClick
        )
    }
}

fun NavController.navigateToNowPlaying() {
    navigate(NowPlayingRoute)
}
