package com.devdunnapps.amplify.ui.artist.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentArtistAllAlbumsBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistAllAlbumsFragment : Fragment() {

    private var _binding: FragmentArtistAllAlbumsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistAllAlbumsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistAllAlbumsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.artistAlbumsCompose.setContent {
            Mdc3Theme {
                ArtistAllAlbumsScreen(
                    viewModel = viewModel,
                    onAlbumClick = { albumId ->
                        findNavController().navigate(MobileNavigationDirections.actionGlobalNavigationAlbum(albumId))
                    }
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.albumsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.albumsToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}

@Composable
private fun ArtistAllAlbumsScreen(viewModel: ArtistAllAlbumsViewModel, onAlbumClick: (String) -> Unit) {
    when (val state = viewModel.artistAlbums.collectAsState().value) {
        is Resource.Loading -> LoadingScreen()
        is Resource.Success -> ArtistAllAlbumsList(state.data!!, onAlbumClick)
        is Resource.Error -> ErrorScreen()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ArtistAllAlbumsList(albums: List<Album>, onAlbumClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(albums) {
            AlbumCard(
                onClick = { onAlbumClick(it.id) },
                album = it
            )
        }
    }
}
