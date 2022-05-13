package com.devdunnapps.amplify.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentAlbumsBinding
import com.devdunnapps.amplify.ui.components.AlbumList
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsFragment : Fragment(){

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumsViewModel by viewModels()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.albumsCompose.setContent {
            Mdc3Theme {
                Surface(
                    modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
                ) {
                    AlbumsScreen(
                        viewModel = viewModel,
                        onClick = { album ->
                            findNavController().navigate(MobileNavigationDirections.actionGlobalNavigationAlbum(album))

                        }
                    )
                }
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

@Composable
private fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onClick: (String) -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    when (albums) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Success -> {
            AlbumList(
                albums = albums.data!!,
                onItemClick = onClick
            )
        }
        is Resource.Error -> {
            ErrorScreen()
        }
    }
}
