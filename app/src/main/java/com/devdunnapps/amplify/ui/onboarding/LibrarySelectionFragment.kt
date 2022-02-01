package com.devdunnapps.amplify.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibrarySelectionFragment: Fragment() {

    val viewModel: LoginFlowViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        viewModel.server.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                viewModel.getLibraries()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    LibrarySelectionScreen(viewModel = viewModel) { libraryKey ->
                        viewModel.selectLibrary(libraryKey)

                        val mainActivityIntent = Intent(activity, MainActivity::class.java)
                        startActivity(mainActivityIntent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }
}

@Composable
private fun LibrarySelectionScreen(viewModel: LoginFlowViewModel, onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Select Library",
            style = MaterialTheme.typography.headlineLarge
        )

        val librarySections by viewModel.libraries.observeAsState()
        if (librarySections is Resource.Success) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                items((librarySections as Resource.Success<List<LibrarySection>>).data!!) { section ->
                    LibraryItem(
                        librarySection = section,
                        onClick = onItemClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LibraryItem(librarySection: LibrarySection, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(librarySection.key) }
    ){
        Text(
            text = librarySection.title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
