package com.devdunnapps.amplify.ui.albums

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
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.R

private const val ARTWORK_SIZE = 100

@Composable
internal fun AlbumsRoute(
    onAlbumClick: (String) -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val albums = viewModel.albums.collectAsLazyPagingItems()
    AlbumsScreen(
        albums = albums,
        topBarActions = topBarActions,
        onAlbumClick = onAlbumClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumsScreen(
    albums: LazyPagingItems<Album>,
    topBarActions: @Composable RowScope.() -> Unit,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (albums.loadState.refresh is LoadState.Error)
        ErrorScreen()

    if (albums.loadState.refresh is LoadState.Loading)
        LoadingScreen(modifier = modifier)

    Scaffold(
        topBar = {
            RootDestinationAppBar(
                title = stringResource(id = R.string.albums_tab_title),
                actions = topBarActions,
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(ARTWORK_SIZE.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
            ) {
                items(albums.itemCount) { index ->
                    albums[index]?.let {
                        AlbumCard(
                            onClick = { onAlbumClick(it.id) },
                            album = it,
                            artworkSize = null
                        )
                    }
                }
            }
        }
    }
}
