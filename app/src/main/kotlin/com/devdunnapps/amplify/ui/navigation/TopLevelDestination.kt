package com.devdunnapps.amplify.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TopLevelDestination<T : Any>(
    val route: T,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconText: Int
)
