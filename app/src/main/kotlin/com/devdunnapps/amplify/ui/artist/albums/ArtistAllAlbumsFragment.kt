package com.devdunnapps.amplify.ui.artist.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.utils.FragmentSubDestinationScaffold
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

private const val ARTWORK_SIZE = 150

@AndroidEntryPoint
class ArtistAllAlbumsFragment : Fragment() {

    private val viewModel: ArtistAllAlbumsViewModel by viewModels()
    private val args: ArtistAllAlbumsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                val screenTitle = remember { if (args.isSinglesEPs) R.string.singles_and_eps else R.string.albums }

                FragmentSubDestinationScaffold(screenTitle = stringResource(screenTitle)) { paddingValues ->
                    ArtistAllAlbumsRoute(
                        viewModel = viewModel,
                        onAlbumClick = { albumId ->
                            val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albumId)
                            findNavController().navigate(action)
                        },
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    )
                }
            }
        }
}

@Composable
fun ArtistAllAlbumsRoute(
    viewModel: ArtistAllAlbumsViewModel,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ArtistAllAlbumsScreen(
        albums = viewModel.artistAlbums.collectAsState().value,
        onAlbumClick = onAlbumClick,
        modifier = modifier
    )
}

@Composable
private fun ArtistAllAlbumsScreen(
    albums: Resource<List<Album>>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (albums) {
        is Resource.Loading -> LoadingScreen(modifier = modifier)
        is Resource.Success -> ArtistAllAlbumsList(albums.data, onAlbumClick, modifier)
        is Resource.Error -> ErrorScreen(modifier = modifier)
    }
}

@Composable
private fun ArtistAllAlbumsList(albums: List<Album>, onAlbumClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(ARTWORK_SIZE.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(albums) {
            AlbumCard(
                onClick = { onAlbumClick(it.id) },
                artworkSize = ARTWORK_SIZE.dp,
                album = it
            )
        }
    }
}
