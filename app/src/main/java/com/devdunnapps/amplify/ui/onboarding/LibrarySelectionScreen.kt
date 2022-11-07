package com.devdunnapps.amplify.ui.onboarding

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource

@Composable
internal fun LibrarySelectionScreen(viewModel: LoginFlowViewModel, onFinishOnboarding: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.select_library),
            style = MaterialTheme.typography.displayMedium
        )

        val librarySections by viewModel.libraries.collectAsState()
        when (librarySections) {
            is Resource.Loading -> LoadingPager()
            is Resource.Error -> ErrorScreen()
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    items(librarySections.data!!) { library ->
                        LibraryItem(
                            librarySection = library,
                            onClick = {
                                viewModel.selectLibrary(library.key)
                                onFinishOnboarding()
                            }
                        )
                    }
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
