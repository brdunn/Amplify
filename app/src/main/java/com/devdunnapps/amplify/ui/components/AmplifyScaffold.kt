package com.devdunnapps.amplify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.google.android.material.composethemeadapter3.Mdc3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmplifyScaffold(
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Mdc3Theme {
        Scaffold(
            topBar = topBar,
            content = content
        )
    }
}
