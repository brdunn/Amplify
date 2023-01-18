package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devdunnapps.amplify.ui.theme.Theme

@Composable
fun AmplifyScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Theme {
        Scaffold(
            topBar = topBar,
            content = content,
            floatingActionButton = floatingActionButton,
            modifier = modifier
        )
    }
}
