package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.material.composethemeadapter3.Mdc3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmplifyScaffold(
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    title: String
) {
    ProvideWindowInsets(consumeWindowInsets = false) {
        Mdc3Theme {
            Scaffold(
                topBar = {
                    AmplifyAppBar(
                        title = title,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToAbout = onNavigateToAbout
                    )
                },
                content = content
            )
        }
    }
}
