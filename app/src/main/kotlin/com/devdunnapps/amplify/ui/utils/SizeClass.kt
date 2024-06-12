package com.devdunnapps.amplify.ui.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.devdunnapps.amplify.utils.getActivity

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun getCurrentSizeClass(): WindowWidthSizeClass {
    val activity = LocalContext.current.getActivity() ?: return WindowWidthSizeClass.Compact
    return calculateWindowSizeClass(activity).widthSizeClass
}
