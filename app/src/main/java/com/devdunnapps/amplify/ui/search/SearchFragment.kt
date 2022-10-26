package com.devdunnapps.amplify.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentSearchBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.AlbumCard
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

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.search.requestFocus()
        binding.search.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString())
        }

        binding.searchResults.setContent {
            when (val result = viewModel.searchResults.collectAsState().value) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Error -> ErrorScreen()
                is Resource.Success ->
                    SearchResultsContent(results = result.data!!, onPlaySong = { viewModel.playSong(it) })
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // automatically show the software keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.search, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.forEach { it.isVisible = false }
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        setHasOptionsMenu(true)
    }
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
        LazyColumn(modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
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
