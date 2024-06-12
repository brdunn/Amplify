package com.devdunnapps.amplify.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource

@Composable
internal fun ServerSelectionScreen(viewModel: LoginFlowViewModel, onNavigateToLibrarySelection: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.select_server),
            style = MaterialTheme.typography.displayMedium
        )

        when (val servers = viewModel.servers.collectAsState().value) {
            is Resource.Loading -> LoadingPager()
            is Resource.Error -> ErrorScreen()
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(servers.data) { server ->
                        ServerItem(
                            server = server,
                            onClick = {
                                viewModel.selectServer(server)
                                onNavigateToLibrarySelection()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerItem(server: Server, onClick: (Server) -> Unit) {
    Card(
        onClick = { onClick(server) }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Server Name: ${server.name}")

            Text(text = "Remote Access: ${!server.localConnectionsOnly}")

            Text(text = "Connect Via Proxy: ${server.proxyConnectionsAllowed}")
        }
    }
}
