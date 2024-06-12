package com.devdunnapps.amplify.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.ui.components.ArtistCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.utils.FragmentRootDestinationScaffold
import dagger.hilt.android.AndroidEntryPoint

private const val ARTWORK_SIZE = 100

@AndroidEntryPoint
class ArtistsFragment : Fragment() {

    private val viewModel: ArtistsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                FragmentRootDestinationScaffold(screenTitle = stringResource(R.string.artists)) { paddingValues ->
                    ArtistsRoute(
                        viewModel = viewModel,
                        onArtistClick = { artist ->
                            findNavController().navigate(MobileNavigationDirections.actionGlobalNavigationArtist(artist))
                        },
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    )
                }
            }
        }
}

@Composable
private fun ArtistsRoute(
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val artists = viewModel.artists.collectAsLazyPagingItems()
    ArtistsScreen(artists = artists, onArtistClick = onArtistClick, modifier = modifier)
}

@Composable
private fun ArtistsScreen(
    artists: LazyPagingItems<Artist>,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (artists.loadState.refresh is LoadState.Error)
        ErrorScreen()

    if (artists.loadState.refresh is LoadState.Loading)
        LoadingScreen(modifier = modifier)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(ARTWORK_SIZE.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(artists.itemCount) { index ->
            artists[index]?.let {
                ArtistCard(
                    onClick = { onArtistClick(it.id) },
                    artist = it,
                    artworkSize = ARTWORK_SIZE.dp
                )
            }
        }
    }
}
