package com.devdunnapps.amplify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.ArtistCard
import com.devdunnapps.amplify.ui.components.Carousel
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.utils.Greeting
import com.devdunnapps.amplify.ui.utils.getCurrentSizeClass
import com.devdunnapps.amplify.ui.utils.whenTrue
import com.devdunnapps.amplify.utils.Resource

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    topBarActions: @Composable RowScope.() -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    onSongMenuClick: (String) -> Unit
) {
    val homeState = viewModel.uiState.collectAsState().value
    HomeScreen(
        homeState = homeState,
        topBarActions = topBarActions,
        modifier = modifier,
        navigateToArtist = navigateToArtist,
        navigateToAlbum = navigateToAlbum,
        playSong = viewModel::playSong,
        onSongMenuClick = onSongMenuClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    homeState: Resource<HomeUIModel>,
    topBarActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    playSong: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            RootDestinationAppBar(
                title = stringResource(id = R.string.app_name),
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
            when (homeState) {
                Resource.Loading -> LoadingScreen(modifier)
                is Resource.Success -> HomeContent(
                    uiModel = homeState.data,
                    navigateToArtist = navigateToArtist,
                    navigateToAlbum = navigateToAlbum,
                    playSong = playSong,
                    onSongMenuClick = onSongMenuClick,
                    modifier = modifier
                )

                is Resource.Error -> ErrorScreen(modifier)
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiModel: HomeUIModel,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    playSong: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        item {
            Header(model = uiModel)
        }

        item {
            Carousel(title = stringResource(R.string.home_recently_played_carousel_header_title)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiModel.recentArtists) { artist ->
                        ArtistCard(
                            artist = artist,
                            artworkSize = 125.dp,
                            onClick = { navigateToArtist(artist.id) }
                        )
                    }
                }
            }
        }

        item {
            Carousel(title = stringResource(R.string.home_recently_added_carousel_header_title)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiModel.recentlyAdded) { album ->
                        AlbumCard(
                            album = album,
                            artworkSize = 125.dp,
                            onClick = { navigateToAlbum(album.id) }
                        )
                    }
                }
            }
        }

        item {
            Carousel(title = stringResource(R.string.home_recent_songs_header_title)) {
                Column {
                    uiModel.recentSongs.forEach { song ->
                        SongItem(
                            song = song,
                            onClick = { playSong(song) },
                            onItemMenuClick = onSongMenuClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(model: HomeUIModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .whenTrue(getCurrentSizeClass() == WindowWidthSizeClass.Compact) {
                fillMaxWidth()
            }
            .whenTrue(getCurrentSizeClass() != WindowWidthSizeClass.Compact) {
                width(400.dp)
            }
            .padding(horizontal = 16.dp)
            .background(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(8.dp)
    ) {
        AsyncImage(
            model = model.userAvatar,
            contentDescription = null,
            modifier = Modifier
                .size(75.dp)
                .clip(shape = CircleShape)
        )

        Text(
            text = stringResource(Greeting.create(), model.userTitle),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
