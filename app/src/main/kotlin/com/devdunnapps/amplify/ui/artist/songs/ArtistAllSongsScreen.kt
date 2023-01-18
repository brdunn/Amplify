package com.devdunnapps.amplify.ui.artist.songs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.components.SubDestinationAppBar
import com.devdunnapps.amplify.utils.Resource

@Composable
fun ArtistAllSongsRoute(
    viewModel: ArtistAllSongsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ArtistAllSongsScreen(
        songs = viewModel.artistSongs.collectAsState().value,
        onBackClick = onBackClick,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistAllSongsScreen(
    songs: Resource<List<Song>>,
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            SubDestinationAppBar(
                title = stringResource(R.string.songs),
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
            when (songs) {
                is Resource.Loading -> LoadingScreen(modifier = modifier)
                is Resource.Success -> ArtistAllSongsList(
                    songs = songs.data,
                    onItemClick = onSongClick,
                    onItemMenuClick = onSongMenuClick,
                    modifier = modifier
                )

                is Resource.Error -> ErrorScreen(modifier = modifier)
            }
        }
    }
}

@Composable
fun ArtistAllSongsList(
    songs: List<Song>,
    onItemClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = songs, key = { it.id }) { song ->
            SongItem(
                onClick = { onItemClick(song) },
                song = song,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
