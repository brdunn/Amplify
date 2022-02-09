package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun AmplifyAppBar(
    title: String,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value

    Box(modifier = Modifier.background(backgroundColor)) {
        SmallTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = { Text(text = title) },
            actions = {
                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    DropdownMenuItem(onClick = onNavigateToSettings) {
                        Text(text = "Settings")
                    }
                    DropdownMenuItem(onClick = onNavigateToAbout) {
                        Text(text = "About")
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun AmplifyAppBarPreview() {
    Mdc3Theme {
        AmplifyAppBar(
            title = "App Bar",
            onNavigateToSearch = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {}
        )
    }
}
