package com.devdunnapps.amplify.ui.artist.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.utils.FragmentSubDestinationScaffold
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistAllSongsFragment : Fragment() {

    private val viewModel: ArtistAllSongsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                FragmentSubDestinationScaffold(screenTitle = stringResource(R.string.albums)) { paddingValues ->
                    ArtistAllSongsRoute(
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
fun ArtistAllSongsRoute(
    viewModel: ArtistAllSongsViewModel,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ArtistAllSongsScreen(
        songs = viewModel.artistSongs.collectAsState().value,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        modifier = modifier
    )
}

@Composable
private fun ArtistAllSongsScreen(
    songs: Resource<List<Song>>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (songs) {
        is Resource.Loading -> LoadingScreen(modifier = modifier)
        is Resource.Success -> ArtistAllSongsList(
            songs = songs.data,
            onItemClick = onSongClick,
            onItemMenuClick = onSongMenuClick,
            modifier = modifier
        )
        is Resource.Error -> ErrorScreen(modifier = modifier)
    }
}

@Composable
fun ArtistAllSongsList(
    songs: List<Song>,
    onItemClick: (Song) -> Unit,
    onItemMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = songs, key = { it.id }) { song ->
            SongItem(
                onClick = { onItemClick(song) },
                song = song,
                onItemMenuClick = onItemMenuClick
            )
        }
    }
}
