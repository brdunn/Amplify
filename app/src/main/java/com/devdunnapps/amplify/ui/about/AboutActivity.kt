package com.devdunnapps.amplify.ui.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.devdunnapps.amplify.R
import com.google.android.material.composethemeadapter3.Mdc3Theme

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Mdc3Theme {
                AboutScreen(onNavigateUp = ::onNavigateUp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(onNavigateUp: () -> Unit) {
    Scaffold(
        topBar = { AboutTopBar(onNavigateUp = onNavigateUp) },
        content = { contentPadding ->
            AboutContent(modifier = Modifier.padding(contentPadding))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutTopBar(onNavigateUp: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = stringResource(R.string.app_name)) },
    )
}

@Composable
private fun AboutContent(modifier: Modifier = Modifier)  {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)

        Text(text = stringResource(R.string.app_version), style = MaterialTheme.typography.bodyMedium)
    }
}
