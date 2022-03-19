package com.devdunnapps.amplify.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentSongsBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.*
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment() {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.songsCompose.setContent {
            Mdc3Theme {
                Surface(
                    modifier = Modifier.nestedScroll((rememberViewInteropNestedScrollConnection()))
                ) {
                    SongsScreen(
                        viewModel = viewModel,
                        onClick = { song ->
                            viewModel.playSong(song)
                        },
                        onItemMenuClick = { songId ->
                            val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songId)
                            findNavController().navigate(action)
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.songsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.songsToolbar)
    }
}

@Composable
private fun SongsScreen(
    viewModel: SongsViewModel,
    onClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    val songs by viewModel.songs.observeAsState()
    when (songs) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Success -> {
            SongsList(
                songs = (songs as Resource.Success<List<Song>>).data!!,
                onItemClick = onClick,
                onItemMenuClick = onItemMenuClick
            )
        }
        is Resource.Error -> {
            ErrorScreen()
        }
    }
}
