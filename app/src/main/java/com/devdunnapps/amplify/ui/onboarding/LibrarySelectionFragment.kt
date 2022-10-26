package com.devdunnapps.amplify.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibrarySelectionFragment: Fragment() {

    val viewModel: LoginFlowViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
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

@Composable
private fun LibrarySelectionScreen(viewModel: LoginFlowViewModel, onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(R.string.select_library),
            style = MaterialTheme.typography.headlineLarge
        )

        val librarySections by viewModel.libraries.collectAsState()
        if (librarySections is Resource.Success) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                items(librarySections.data!!) { section ->
                    LibraryItem(
                        librarySection = section,
                        onClick = onItemClick
                    )
                }
            }
        }
    }
}

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
