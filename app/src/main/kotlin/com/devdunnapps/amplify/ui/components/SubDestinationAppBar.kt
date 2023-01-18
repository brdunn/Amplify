package com.devdunnapps.amplify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.devdunnapps.amplify.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubDestinationAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = title, maxLines = 1) },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SubDestinationAppBarPreview() {
    Theme {
        SubDestinationAppBar(
            title = "App Bar",
            onNavigateBack = {},
        )
    }
}
