package com.devdunnapps.amplify.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentAlbumsBinding
import com.devdunnapps.amplify.ui.components.AlbumCard
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsFragment : Fragment(){

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.albumsCompose.setContent {
            Mdc3Theme {
                AlbumsScreen(
                    viewModel = viewModel,
                    onClick = { album ->
                        findNavController().navigate(MobileNavigationDirections.actionGlobalNavigationAlbum(album))
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
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onClick: (String) -> Unit
) {
    val albums = viewModel.albums.collectAsLazyPagingItems()

    if (albums.loadState.refresh is LoadState.Loading)
        LoadingScreen()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(albums.itemCount) { index ->
            albums[index]?.let {
                AlbumCard(
                    onClick = { onClick(it.id) },
                    album = it
                )
            }
        }
    }
}
