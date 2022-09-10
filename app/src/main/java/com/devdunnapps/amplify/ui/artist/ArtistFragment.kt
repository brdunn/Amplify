package com.devdunnapps.amplify.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import coil.load
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentArtistBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.components.Carousel
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtistViewModel by viewModels()
    private val args: ArtistFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        setSystemUI()

        loadArtist()
        loadArtistTopAlbums()
        loadArtistTopSinglesEps()
        loadArtistTopSongs()

        binding.artistShuffleBtn.setOnClickListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadArtistTopAlbums() {
        binding.artistAlbumsComposeView.setContent {
            Mdc3Theme {
                val state = viewModel.artistAlbums.collectAsState().value
                if (state is Resource.Success) {
                    if (state.data?.isNotEmpty() == true) {
                        Carousel(
                            title = stringResource(R.string.artist_albums_header),
                            modifier = Modifier.padding(top = 32.dp),
                            onViewAllClick = {
                                val action = ArtistFragmentDirections
                                    .actionNavigationArtistToNavigationArtistAllAlbums(args.artistKey)
                                findNavController().navigate(action)
                            }
                        ) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.data) { album ->
                                    ArtistAlbumCard(
                                        album = album,
                                        onClick = {
                                            val action = MobileNavigationDirections
                                                .actionGlobalNavigationAlbum(album.id)
                                            findNavController().navigate(action)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadArtistTopSinglesEps() {
        binding.artistEpsSinglesComposeView.setContent {
            Mdc3Theme {
                val state = viewModel.artistSinglesEPs.collectAsState().value
                if (state is Resource.Success) {
                    if (state.data?.isNotEmpty() == true) {
                        Carousel(
                            title = stringResource(R.string.artist_singles_eps_header),
                            modifier = Modifier.padding(top = 32.dp),
                            onViewAllClick = {
                                val action = ArtistFragmentDirections
                                    .actionNavigationArtistToNavigationArtistAllAlbums(args.artistKey)
                                findNavController().navigate(action)
                            }
                        ) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.data) { album ->
                                    ArtistAlbumCard(
                                        album = album,
                                        onClick = {
                                            val action = MobileNavigationDirections
                                                .actionGlobalNavigationAlbum(album.id)
                                            findNavController().navigate(action)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadArtist() {
        viewModel.artist.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val artist = result.data!!

                binding.artistToolbar.title = artist.name
                binding.artistName.text = artist.name

                binding.artistBioCompose.setContent {
                    Mdc3Theme {
                        ExpandableText(artist.bio)
                    }
                }

                val imageUrl = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(artist.thumb)
                binding.artistPicture.load(imageUrl) {
                    placeholder(R.drawable.ic_artists_black_24dp)
                }
            }
        }
    }

    private fun loadArtistTopSongs() {
        binding.artistTopSongsComposeView.setContent {
            Mdc3Theme {
                val state = viewModel.artistSongs.collectAsState().value
                if (state is Resource.Success) {
                    if (state.data?.isNotEmpty() == true) {
                        Carousel(
                            title = stringResource(R.string.artist_top_songs_header),
                            modifier = Modifier.padding(top = 32.dp),
                            onViewAllClick = {
                                val action = ArtistFragmentDirections
                                    .actionNavigationArtistToNavigationArtistAllSongs(args.artistKey)
                                findNavController().navigate(action)
                            }
                        ) {
                            Column {
                                for (i in 0 until 5) {
                                    val song = state.data[i]
                                    SongItem(
                                        song = song,
                                        onClick = { viewModel.playSong(song) },
                                        onItemMenuClick = {
                                            val action = MobileNavigationDirections
                                                .actionGlobalNavigationSongBottomSheet(song.id)
                                            findNavController().navigate(action)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.artist_shuffle_btn -> {
                viewModel.shuffleArtist()
            }
        }
    }
}

@Composable
fun ArtistAlbumCard(onClick: () -> Unit, album: Album) {
    Column(modifier = Modifier.requiredWidth(200.dp).clickable { onClick() }) {
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
