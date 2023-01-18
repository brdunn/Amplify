package com.devdunnapps.amplify.ui.songs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.ui.components.SongItem

@Composable
internal fun SongsRoute(
    onSongMenuClick: (String) -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val songs = viewModel.songs.collectAsLazyPagingItems()
    SongsScreen(
        songs = songs,
        topBarActions = topBarActions,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongsScreen(
    songs: LazyPagingItems<Song>,
    topBarActions: @Composable RowScope.() -> Unit,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            RootDestinationAppBar(
                title = stringResource(id = R.string.songs_tab_title),
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
            if (songs.loadState.refresh is LoadState.Error)
                ErrorScreen()

            if (songs.loadState.refresh is LoadState.Loading)
                LoadingScreen(modifier = modifier)

            LazyColumn(
                modifier = modifier
            ) {
                items(items = songs, key = { song -> song.id }) { song ->
                    song?.let {
                        SongItem(
                            onClick = { onSongClick(it) },
                            song = it,
                            onItemMenuClick = onSongMenuClick
                        )
                    }
                }

                if (songs.loadState.append is LoadState.Loading)
                    item { LoadingPager() }
            }
        }
    }
}
