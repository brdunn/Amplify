package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.ui.components.ZeroStateScreen
import com.devdunnapps.amplify.ui.utils.FragmentSubDestinationScaffold
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModels()
    private val args: PlaylistFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                FragmentSubDestinationScaffold(screenTitle = stringResource(R.string.playlists)) { paddingValues ->
                    PlaylistRoute(
                        viewModel = viewModel,
                        onSongMenuClick = { song ->
                            val action = PlaylistFragmentDirections
                                .actionGlobalNavigationSongBottomSheet(songId = song.id, playlistId = args.playlistId)
                            findNavController().navigate(action)
                        },
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    )
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.navigation_playlist)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("refreshData")) {
                val songsShouldBeRefreshed = navBackStackEntry.savedStateHandle.get<Boolean>("refreshData")!!
                if (songsShouldBeRefreshed) {
                    viewModel.refresh()
                }
            }
        }
        navBackStackEntry.getLifecycle().addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.getLifecycle().removeObserver(observer)
            }
        })
    }
}

@Composable
fun PlaylistRoute(viewModel: PlaylistViewModel, onSongMenuClick: (Song) -> Unit, modifier: Modifier = Modifier) {
    PlaylistScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onSongClick = viewModel::playSong,
        onSongMenuClick = onSongMenuClick,
        editPlaylistMetadata = viewModel::editPlaylistMetadata,
        onPlayClick = viewModel::playPlaylist,
        onShuffleClick = { viewModel.playPlaylist(shuffle = true) },
        modifier = modifier
    )
}

@Composable
private fun PlaylistScreen(
    uiState: Resource<PlaylistUIModel>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    editPlaylistMetadata: (String, String) -> Unit,
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is Resource.Loading -> LoadingScreen(modifier = modifier)
        is Resource.Error -> ErrorScreen(modifier = modifier)
        is Resource.Success -> PlaylistContent(
            uiModel = uiState.data,
            onSongClick = onSongClick,
            onSongMenuClick = onSongMenuClick,
            editPlaylistMetadata = editPlaylistMetadata,
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick,
            modifier = modifier
        )
    }
}

@Composable
private fun PlaylistContent(
    uiModel: PlaylistUIModel,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    editPlaylistMetadata: (String, String) -> Unit,
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditPlaylistDialogVisible by remember { mutableStateOf(false) }

    if (uiModel.songs.isEmpty()) {
        PlaylistZeroState(modifier = modifier)
    } else {
        LazyColumn(modifier = modifier) {
            item {
                PlaylistHeader(
                    playlist = uiModel.playlist,
                    onEditPlaylistClicked = { isEditPlaylistDialogVisible = true },
                    onPlayClick = onPlayClick,
                    onShuffleClick = onShuffleClick
                )
            }


            items(uiModel.songs) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song) },
                    onItemMenuClick = { onSongMenuClick(song) }
                )
            }
        }
    }

    if (isEditPlaylistDialogVisible) {
        EditPlaylistDialog(
            playlist = uiModel.playlist,
            onSaveClicked = { title, summary ->
                editPlaylistMetadata(title, summary)
                isEditPlaylistDialogVisible = false
            },
            dismiss = { isEditPlaylistDialogVisible = false }
        )
    }
}

@Composable
private fun PlaylistHeader(
    playlist: Playlist,
    onEditPlaylistClicked: () -> Unit,
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = PlexUtils.getInstance(LocalContext.current).getSizedImage(playlist.composite),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        ExpandableText(text = playlist.summary)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
            ) {
            IconButton(onClick = onEditPlaylistClicked) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
            }

            FloatingActionButton(onClick = onPlayClick) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
            }

            IconButton(onClick = onShuffleClick) {
                Icon(imageVector = Icons.Outlined.Shuffle, contentDescription = null)
            }
        }
    }
}

@Composable
private fun PlaylistZeroState(modifier: Modifier = Modifier) =
    ZeroStateScreen(title = R.string.playlist_zero_state_title, modifier = modifier)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPlaylistDialog(
    playlist: Playlist,
    onSaveClicked: (String, String) -> Unit,
    dismiss: () -> Unit
) {
    var title by remember(playlist.title) { mutableStateOf(playlist.title) }
    var summary by remember(playlist.summary) { mutableStateOf(playlist.summary) }

    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.edit_playlist))
        },
        text = {
           Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
               OutlinedTextField(
                   value = title,
                   onValueChange = { title = it},
                   maxLines = 1,
                   label = { Text(text = stringResource(id = R.string.title)) },
                   keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
               )

               OutlinedTextField(
                   value = summary,
                   onValueChange = { summary = it},
                   label = { Text(text = stringResource(id = R.string.description)) },
                   keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                   keyboardActions = KeyboardActions(onDone = { onSaveClicked(title, summary) })
               )
           }
        },
        onDismissRequest = dismiss,
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveClicked(title, summary) }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    )
}

@Preview
@Composable
private fun EditPlaylistDialogPreview() {
    EditPlaylistDialog(
        playlist = Playlist(
            id = "",
            title = "Playlist Title",
            summary = "This is a playlist of songs I like to listen to",
            numSongs = 0,
            composite = ""
        ),
        onSaveClicked = { _, _ -> },
        dismiss = {}
    )
}
