package com.devdunnapps.amplify.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServerSelectionFragment: Fragment() {

    val viewModel: LoginFlowViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    ServerSelectionScreen(viewModel = viewModel) { server ->
                        viewModel.selectServer(server)

                        val action = ServerSelectionFragmentDirections.actionServerSelectionFragmentToLibrarySelectionFragment()
                        findNavController().navigate(action)

                        (requireActivity() as OnBoardingActivity).updateIndicators { it + 1 }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerSelectionScreen(viewModel: LoginFlowViewModel, onItemClick: (Server) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Server",
            style = MaterialTheme.typography.headlineLarge
        )

        val servers by viewModel.servers.observeAsState()
        if (servers is Resource.Success) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.75f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((servers as Resource.Success<List<Server>>).data!!) { section ->
                    ServerItem(
                        server = section,
                        onClick = onItemClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ServerItem(server: Server, onClick: (Server) -> Unit) {
    Button(
        onClick = { onClick(server) }
    ) {
        Text(
            text = server.address,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
