package com.devdunnapps.amplify.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.*
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource

@Composable
internal fun ArtistRoute(
    viewModel: ArtistViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onViewAllAlbumsClick: () -> Unit,
    onViewAllSinglesEPsClick: () -> Unit,
    onSongMenuClick: (String) -> Unit,
    onViewAllSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ArtistScreen(
        artist = viewModel.artist.collectAsState().value,
        onBackClick = onBackClick,
        onShuffleArtistClick = viewModel::shuffleArtist,
        artistAlbums = viewModel.artistAlbums.collectAsState().value,
        artistSinglesEPs = viewModel.artistSinglesEPs.collectAsState().value,
        onAlbumClick = onAlbumClick,
        onViewAllAlbumsClick = onViewAllAlbumsClick,
        onViewAllSinglesEPsClick = onViewAllSinglesEPsClick,
        artistTopSongs = viewModel.artistSongs.collectAsState().value,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        onViewAllSongsClick = onViewAllSongsClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistScreen(
    artist: Resource<Artist>,
    onBackClick: () -> Unit,
    onShuffleArtistClick: () -> Unit,
    artistAlbums: Resource<List<Album>>,
    artistSinglesEPs: Resource<List<Album>>,
    onAlbumClick: (String) -> Unit,
    onViewAllAlbumsClick: () -> Unit,
    onViewAllSinglesEPsClick: () -> Unit,
    artistTopSongs: Resource<List<Song>>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    onViewAllSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var title by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            SubDestinationAppBar(
                title = title,
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
            when (artist) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Error -> ErrorScreen()
                is Resource.Success -> {
                    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                        SideEffect { title = artist.data.name }

                        ArtistHeader(artist = artist.data, onShuffleClick = onShuffleArtistClick)

                        if (artistAlbums is Resource.Success) {
                            if (artistAlbums.data.isNotEmpty()) {
                                ArtistAlbumsCarousel(
                                    albums = artistAlbums.data,
                                    onAlbumClick = onAlbumClick,
                                    onViewAllClick = onViewAllAlbumsClick
                                )
                            }
                        }

                        if (artistSinglesEPs is Resource.Success) {
                            if (artistSinglesEPs.data.isNotEmpty()) {
                                ArtistEPsSinglesCarousel(
                                    albums = artistSinglesEPs.data,
                                    onAlbumClick = onAlbumClick,
                                    onViewAllClick = onViewAllSinglesEPsClick
                                )
                            }
                        }

                        if (artistTopSongs is Resource.Success) {
                            if (artistTopSongs.data.isNotEmpty()) {
                                ArtistSongsCarousel(
                                    songs = artistTopSongs.data.subList(
                                        0,
                                        minOf(5, artistTopSongs.data.size)
                                    ),
                                    onSongClick = onSongClick,
                                    onSongMenuClick = onSongMenuClick,
                                    onViewAllClick = onViewAllSongsClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistHeader(artist: Artist, onShuffleClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            val context = LocalContext.current
            val imageUrl =
                PlexUtils.getInstance(context).addKeyAndAddress(artist.art ?: artist.thumb)
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3 / 2f)
            )

            Text(
                text = artist.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(horizontal = 16.dp)
            )
        }

        Button(
            onClick = onShuffleClick
        ) {
            Text(
                text = stringResource(R.string.shuffle),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        ExpandableText(text = artist.bio)
    }
}

@Composable
private fun ArtistAlbumsCarousel(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Carousel(
        title = stringResource(R.string.artist_albums_header),
        modifier = Modifier.padding(top = 32.dp),
        onViewAllClick = onViewAllClick
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(albums) { album ->
                ArtistAlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
    }
}

@Composable
private fun ArtistEPsSinglesCarousel(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Carousel(
        title = stringResource(R.string.artist_singles_eps_header),
        modifier = Modifier.padding(top = 32.dp),
        onViewAllClick = onViewAllClick
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(albums) { album ->
                ArtistAlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
    }
}

@Composable
private fun ArtistSongsCarousel(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Carousel(
        title = stringResource(R.string.artist_top_songs_header),
        modifier = Modifier.padding(top = 32.dp),
        onViewAllClick = onViewAllClick
    ) {
        Column {
            songs.forEach { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song) },
                    onItemMenuClick = { onSongMenuClick(song.id) }
                )
            }
        }
    }
}

@Composable
private fun ArtistAlbumCard(onClick: () -> Unit, album: Album) {
    Column(modifier = Modifier
        .requiredWidth(200.dp)
        .clickable { onClick() }) {
        val context = LocalContext.current
        val imageUrl = remember {
            PlexUtils.getInstance(context).getSizedImage(album.thumb, 300, 300)
        }
        AsyncImage(
            model = imageUrl,
            placeholder = painterResource(R.drawable.ic_album),
            error = painterResource(R.drawable.ic_album),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.aspectRatio(1f),
        )

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(
            text = "Album • ${album.year}",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
