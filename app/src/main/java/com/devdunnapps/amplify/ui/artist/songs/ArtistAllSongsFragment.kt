package com.devdunnapps.amplify.ui.artist.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.devdunnapps.amplify.databinding.FragmentArtistAllSongsBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistAllSongsFragment : Fragment() {

    private var _binding: FragmentArtistAllSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistAllSongsViewModel by viewModels()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistAllSongsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.artistAllSongsCompose.setContent {
            Mdc3Theme {
                Surface(
                    modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
                ) {
                    ArtistAllSongsScreen(
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.artistAllSongsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.artistAllSongsToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}

@Composable
private fun ArtistAllSongsScreen(
    viewModel: ArtistAllSongsViewModel,
    onClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    when (val songs = viewModel.artistSongs.observeAsState(Resource.Loading()).value) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Success -> {
            ArtistAllSongsList(
                songs = songs.data!!,
                onItemClick = onClick,
                onItemMenuClick = onItemMenuClick
            )
        }
        is Resource.Error -> {
            ErrorScreen()
        }
    }
}

@Composable
fun ArtistAllSongsList(
    songs: List<Song>,
    onItemClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    LazyColumn {
        items(items = songs, key = { it.id }) { song ->
            SongItem(
                onClick = { onItemClick(song) },
                song = song,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
