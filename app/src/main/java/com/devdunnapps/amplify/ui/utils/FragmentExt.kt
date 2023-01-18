package com.devdunnapps.amplify.ui.utils

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.ui.about.AboutActivity
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.ui.components.AmplifyScaffold
import com.devdunnapps.amplify.ui.components.SubDestinationAppBar
import com.devdunnapps.amplify.ui.settings.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.FragmentRootDestinationScaffold(
    screenTitle: String,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AmplifyScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RootDestinationAppBar(
                title = screenTitle,
                onNavigateToSearch = {
                    val action = MobileNavigationDirections.actionGlobalSearchFragment()
                    findNavController().navigate(action)
                },
                onNavigateToSettings = {
                    val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(settingsIntent)
                },
                onNavigateToAbout = {
                    val aboutIntent = Intent(requireContext(), AboutActivity::class.java)
                    startActivity(aboutIntent)
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        content = { content(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.FragmentSubDestinationScaffold(
    screenTitle: String,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AmplifyScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubDestinationAppBar(
                title = screenTitle,
                onNavigateUp = {
                    findNavController().navigateUp()
                },
                onNavigateToSearch = {
                    val action = MobileNavigationDirections.actionGlobalSearchFragment()
                    findNavController().navigate(action)
                },
                onNavigateToSettings = {
                    val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(settingsIntent)
                },
                onNavigateToAbout = {
                    val aboutIntent = Intent(requireContext(), AboutActivity::class.java)
                    startActivity(aboutIntent)
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        content = { content(it) }
    )
}
