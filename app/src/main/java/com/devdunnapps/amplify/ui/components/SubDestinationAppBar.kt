package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.devdunnapps.amplify.R
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubDestinationAppBar(
    title: String,
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = title, maxLines = 1) },
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.statusBars,
        actions = {
            IconButton(onClick = onNavigateToSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }

            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.settings))
                    },
                    onClick = onNavigateToSettings
                )

                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.about))
                    },
                    onClick = onNavigateToAbout
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SubDestinationAppBarPreview() {
    Mdc3Theme {
        SubDestinationAppBar(
            title = "App Bar",
            onNavigateUp = {},
            onNavigateToSearch = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {}
        )
    }
}
