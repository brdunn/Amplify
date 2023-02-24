package com.devdunnapps.amplify.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.utils.FragmentRootDestinationScaffold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment() {

    private val viewModel: SongsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                FragmentRootDestinationScaffold(screenTitle = stringResource(R.string.songs)) { paddingValues ->
                    SongsRoute(
                        viewModel = viewModel,
                        onSongMenuClick = { songId ->
                            val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songId)
                            findNavController().navigate(action)
                        },
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    )
                }
            }
        }
}

@Composable
private fun SongsRoute(
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val songs = viewModel.songs.collectAsLazyPagingItems()
    SongsScreen(
        songs = songs,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        modifier = modifier
    )
}

@Composable
private fun SongsScreen(
    songs: LazyPagingItems<Song>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.loadState.refresh is LoadState.Error)
        ErrorScreen()

    if (songs.loadState.refresh is LoadState.Loading)
        LoadingScreen(modifier = modifier)

    LazyColumn(
        modifier = modifier
    ) {
        items(items = songs, key = { song -> song.id }) { song ->
            song?.let {
                SongItem(
                    onClick = { onSongClick(it) },
                    song = it,
                    onItemMenuClick = onSongMenuClick
                )
            }
        }

        if (songs.loadState.append is LoadState.Loading)
            item { LoadingPager() }
    }
}
