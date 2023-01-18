package com.devdunnapps.amplify.ui.artist.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SubDestinationAppBar
import com.devdunnapps.amplify.utils.Resource

private const val ARTWORK_SIZE = 150

@Composable
fun ArtistAllAlbumsRoute(
    viewModel: ArtistAllAlbumsViewModel = hiltViewModel(),
    isSinglesEPs: Boolean,
    onAlbumClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ArtistAllAlbumsScreen(
        albums = viewModel.artistAlbums.collectAsState().value,
        isSinglesEPs = isSinglesEPs,
        onBackClick = onBackClick,
        onAlbumClick = onAlbumClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistAllAlbumsScreen(
    albums: Resource<List<Album>>,
    isSinglesEPs: Boolean,
    onBackClick: () -> Unit,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            SubDestinationAppBar(
                title = stringResource(
                    id = if (isSinglesEPs) R.string.artist_singles_eps_header else R.string.albums
                ),
                onNavigateBack = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
        ) {

            when (albums) {
                is Resource.Loading -> LoadingScreen(modifier = modifier)
                is Resource.Success -> ArtistAllAlbumsList(albums.data, onAlbumClick, modifier)
                is Resource.Error -> ErrorScreen(modifier = modifier)
            }
        }
    }
}

@Composable
private fun ArtistAllAlbumsList(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                artworkSize = null,
                album = it
            )
        }
    }
}
