package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.devdunnapps.amplify.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootDestinationAppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        scrollBehavior = scrollBehavior,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AmplifyAppBarPreview() {
    Theme {
        RootDestinationAppBar(title = "App Bar")
    }
}
