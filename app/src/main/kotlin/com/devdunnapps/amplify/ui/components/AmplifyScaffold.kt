package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun AmplifyScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Mdc3Theme {
        Scaffold(
            topBar = topBar,
            content = content,
            floatingActionButton = floatingActionButton,
            modifier = modifier
        )
    }
}
