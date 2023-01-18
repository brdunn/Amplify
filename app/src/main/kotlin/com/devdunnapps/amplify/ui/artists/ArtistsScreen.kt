package com.devdunnapps.amplify.ui.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.ui.components.ArtistCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar

private const val ARTWORK_SIZE = 100

@Composable
internal fun ArtistsRoute(
    topBarActions: @Composable RowScope.() -> Unit,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val artists = viewModel.artists.collectAsLazyPagingItems()
    ArtistsScreen(
        artists = artists,
        topBarActions = topBarActions,
        onArtistClick = onArtistClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistsScreen(
    artists: LazyPagingItems<Artist>,
    topBarActions: @Composable RowScope.() -> Unit,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            RootDestinationAppBar(
                title = stringResource(id = R.string.artists),
                actions = topBarActions,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
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
                            artworkSize = null
                        )
                    }
                }
            }
        }
    }
}
