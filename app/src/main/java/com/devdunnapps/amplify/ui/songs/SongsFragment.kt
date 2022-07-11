package com.devdunnapps.amplify.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentSongsBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SongsScreen(
    viewModel: SongsViewModel,
    onClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    val songs = viewModel.songs.collectAsLazyPagingItems()

    if (songs.loadState.refresh is LoadState.Loading)
        LoadingScreen()

    LazyColumn(
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(items = songs, key = { song -> song.id }) { song ->
            song?.let {
                SongItem(
                    onClick = { onClick(it) },
                    song = it,
                    onItemMenuClick = onItemMenuClick
                )
            }
        }

        if (songs.loadState.append is LoadState.Loading)
            item { LoadingPager() }
    }
}
