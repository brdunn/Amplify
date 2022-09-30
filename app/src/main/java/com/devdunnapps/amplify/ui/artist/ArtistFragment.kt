package com.devdunnapps.amplify.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentArtistBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.*
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtistViewModel by viewModels()
    private val args: ArtistFragmentArgs by navArgs()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.artistComposeView.setContent {
            Mdc3Theme {
                when(val artistState = viewModel.artist.collectAsState().value) {
                    is Resource.Loading -> LoadingScreen()
                    is Resource.Error -> ErrorScreen()
                    is Resource.Success -> Column(
                        modifier = Modifier
                            .nestedScroll(rememberNestedScrollInteropConnection())
                            .verticalScroll(rememberScrollState())
                    ) {
                        ArtistHeader(artist = artistState.data!!, onShuffleClick = viewModel::shuffleArtist)

                        val albumsState = viewModel.artistAlbums.collectAsState().value
                        if (albumsState is Resource.Success) {
                            if (albumsState.data?.isNotEmpty() == true) {
                                ArtistAlbumsCarousel(
                                    albums = albumsState.data,
                                    onAlbumClick = { albumId ->
                                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albumId)
                                        findNavController().navigate(action)
                                    },
                                    onViewAllClick = {
                                        val action = ArtistFragmentDirections
                                            .actionNavigationArtistToNavigationArtistAllAlbums(args.artistKey)
                                        findNavController().navigate(action)
                                    }
                                )
                            }
                        }

                        val singlesEPsState = viewModel.artistSinglesEPs.collectAsState().value
                        if (singlesEPsState is Resource.Success) {
                            if (singlesEPsState.data?.isNotEmpty() == true) {
                                ArtistEPsSinglesCarousel(
                                    albums = singlesEPsState.data,
                                    onAlbumClick = { albumId ->
                                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albumId)
                                        findNavController().navigate(action)
                                    },
                                    onViewAllClick = {
                                        val action = ArtistFragmentDirections.actionNavigationArtistToNavigationArtistAllAlbums(
                                            artistId = args.artistKey,
                                            isSinglesEPs = true
                                        )
                                        findNavController().navigate(action)
                                    }
                                )
                            }
                        }

                        val songsState = viewModel.artistSongs.collectAsState().value
                        if (songsState is Resource.Success) {
                            if (songsState.data?.isNotEmpty() == true) {
                                ArtistSongsCarousel(
                                    songs = songsState.data.subList(0, minOf(5, songsState.data.size)),
                                    onSongClick = { song ->
                                        viewModel.playSong(song)
                                    },
                                    onSongMenuClick = { songId ->
                                        val action = MobileNavigationDirections
                                            .actionGlobalNavigationSongBottomSheet(songId)
                                        findNavController().navigate(action)
                                    },
                                    onViewAllClick = {
                                        val action = ArtistFragmentDirections
                                            .actionNavigationArtistToNavigationArtistAllSongs(args.artistKey)
                                        findNavController().navigate(action)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets the toolbar as the appbar, home as up, and system bars
     */
    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.artistToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.artistToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}

@Composable
private fun ArtistHeader(artist: Artist, onShuffleClick: () -> Unit) {
    Column {
        Box {
            ArtistGradientImage(artistImage = artist.thumb)

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(
                    text = artist.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Button(
                    onClick = onShuffleClick,
                    modifier = Modifier
                        .requiredWidth(150.dp)
                        .offset(y = 24.dp)
                ) {
                    Text(text = stringResource(R.string.shuffle))
                }
            }
        }

        ExpandableText(text = artist.bio, modifier = Modifier.padding(top = 56.dp))
    }
}

@Composable
private fun ArtistGradientImage(artistImage: String) {
    val context = LocalContext.current
    val imageUrl = PlexUtils.getInstance(context).addKeyAndAddress(artistImage)
    Box {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(320.dp)

        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(320.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
        )
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
        val imageUrl = remember { PlexUtils.getInstance(context).getSizedImage(album.thumb, 300, 300) }
        AsyncImage(
            model = imageUrl,
            placeholder = painterResource(R.drawable.ic_albums_black_24dp),
            error = painterResource(R.drawable.ic_albums_black_24dp),
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
