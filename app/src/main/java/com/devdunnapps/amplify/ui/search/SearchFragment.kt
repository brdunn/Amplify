package com.devdunnapps.amplify.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.AmplifyScaffold
import com.devdunnapps.amplify.ui.components.ArtistCard
import com.devdunnapps.amplify.ui.components.Carousel
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.PlaylistItem
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.components.ZeroStateScreen
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)

        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    searchResults = viewModel.searchResults.collectAsState().value,
                    onNavigateUp = { findNavController().navigateUp() },
                    onTextChanged = viewModel::search,
                    onPlaySong = viewModel::playSong
                )
            }
        }
    }
}

@Composable
private fun SearchScreen(
    searchResults: Resource<SearchResults>,
    onNavigateUp: () -> Unit,
    onTextChanged: (String) -> Unit,
    onPlaySong: (Song) -> Unit
) {
    AmplifyScaffold(
        topBar = {
            SearchAppBar(
                onUpClicked = onNavigateUp,
                onTextChanged = onTextChanged
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (searchResults) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Error -> ErrorScreen()
                is Resource.Success -> SearchResultsContent(results = searchResults.data!!, onPlaySong = onPlaySong)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SearchAppBar(onUpClicked: () -> Unit, onTextChanged: (String) -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onUpClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            var searchTerm by remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            TextField(
                value = searchTerm,
                onValueChange = {
                    searchTerm = it
                    onTextChanged(searchTerm)
                },
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                ),
                placeholder = { Text(text = stringResource(R.string.search_hint)) },
                trailingIcon = {
                    if (searchTerm.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchTerm = ""
                                onTextChanged("")
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchResultsContent(results: SearchResults, onPlaySong: (Song) -> Unit) {
    val songs = results.songs
    val albums = results.albums
    val artists = results.artists
    val playlists = results.playlists
    val zeroStateVisible = songs.isEmpty() && albums.isEmpty() && artists.isEmpty() && playlists.isEmpty()

    if (zeroStateVisible) {
        SearchResultsZeroState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            if (songs.isNotEmpty())
                item { SongsSearchResults(songs = songs, onPlaySong = onPlaySong) }

            if (albums.isNotEmpty())
                item { AlbumsSearchResults(albums = albums) }

            if (artists.isNotEmpty())
                item { ArtistsSearchResults(artists = artists) }

            if (playlists.isNotEmpty())
                item { PlaylistsSearchResults(playlists = playlists) }
        }
    }
}

@Composable
private fun SearchResultsZeroState() =
    ZeroStateScreen(title = R.string.search_zero_state_title)

@Composable
private fun SongsSearchResults(songs: List<Song>, onPlaySong: (Song) -> Unit) {
    val localView = LocalView.current

    Carousel(title = stringResource(R.string.songs)) {
        Column {
            songs.forEach { song ->
                SongItem(
                    song = song,
                    onClick = { onPlaySong(song) },
                    onItemMenuClick = {
                        val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(song.id)
                        localView.findNavController().navigate(action)
                    }
                )
            }
        }
    }
}

@Composable
private fun AlbumsSearchResults(albums: List<Album>) {
    val localView = LocalView.current

    Carousel(title = stringResource(R.string.albums)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(albums) { album ->
                AlbumCard(
                    album = album,
                    onClick = {
                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(album.id)
                        localView.findNavController().navigate(action)
                    },
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
private fun ArtistsSearchResults(artists: List<Artist>) {
    val localView = LocalView.current

    Carousel(title = stringResource(R.string.artists)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(artists) { artist ->
                ArtistCard(
                    artist = artist,
                    onClick = {
                        val action = MobileNavigationDirections.actionGlobalNavigationArtist(artist.id)
                        localView.findNavController().navigate(action)
                    },
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistsSearchResults(playlists: List<Playlist>) {
    val localView = LocalView.current

    Carousel(title = stringResource(R.string.playlists)) {
        Column {
            playlists.forEach { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = {
                        val action = SearchFragmentDirections.actionSearchFragmentToNavigationPlaylist(playlist.id)
                        localView.findNavController().navigate(action)
                    },
                    onItemMenuClick = {
                        val action = MobileNavigationDirections.actionGlobalPlaylistMenuBottomSheetFragment(playlist.id)
                        localView.findNavController().navigate(action)
                    }
                )
            }
        }
    }
}
