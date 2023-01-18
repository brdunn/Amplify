package com.devdunnapps.amplify.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.PlaylistItem
import com.devdunnapps.amplify.ui.utils.FragmentRootDestinationScaffold
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                var isCreatePlaylistDialogVisible by remember { mutableStateOf(false) }

                FragmentRootDestinationScaffold(
                    screenTitle = stringResource(R.string.playlists),
                    floatingActionButton = {
                        FloatingActionButton(onClick = { isCreatePlaylistDialogVisible = true }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    }
                ) { paddingValues ->
                    if (isCreatePlaylistDialogVisible) {
                        CreatePlaylistDialog(
                            onSubmit = {
                                viewModel.createPlaylist(it)
                                isCreatePlaylistDialogVisible = false
                            },
                            onDismiss = { isCreatePlaylistDialogVisible = false }
                        )
                    }

                    PlaylistsRoute(
                        viewModel = viewModel,
                        onPlaylistClick = { playlistId ->
                            val action =
                                PlaylistsFragmentDirections.actionNavigationPlaylistsToPlaylistFragment(playlistId)
                            findNavController().navigate(action)
                        },
                        onPlaylistMenuClick = { playlistId ->
                            val action = PlaylistsFragmentDirections
                                .actionNavigationPlaylistsToPlaylistMenuBottomSheetFragment(playlistId)
                            findNavController().navigate(action)
                        },
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    )
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.navigation_playlists)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val playlistsShouldBeRefreshed = navBackStackEntry.savedStateHandle.get<Boolean>("refreshData") ?: false
                if (playlistsShouldBeRefreshed) {
                    viewModel.gatherPlaylists()
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePlaylistDialog(onSubmit: (String) -> Unit, onDismiss: () -> Unit) {
    var playlistTitle by remember { mutableStateOf("") }
    var isSubmitEnabled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.create_playlist_dialog_title)) },
        text = {
            TextField(
                value = playlistTitle,
                onValueChange = {
                    playlistTitle = it
                    isSubmitEnabled = playlistTitle.isNotBlank()
                },
                maxLines = 1,
                placeholder = { Text(text = stringResource(R.string.create_playlist_dialog_hint)) },
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(playlistTitle) }, enabled = isSubmitEnabled) {
                Text(text = stringResource(R.string.create_playlist_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )

    LaunchedEffect(key1 = true) {
        delay(timeMillis = 100)
        focusRequester.requestFocus()
    }
}

@Composable
private fun PlaylistsRoute(
    onPlaylistClick: (String) -> Unit,
    onPlaylistMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    PlaylistsScreen(
        playlists = viewModel.playlists.collectAsState().value,
        onPlaylistClick = onPlaylistClick,
        onPlaylistMenuClick = onPlaylistMenuClick,
        modifier = modifier
    )
}

@Composable
private fun PlaylistsScreen(
    playlists: Resource<List<Playlist>>,
    onPlaylistClick: (String) -> Unit,
    onPlaylistMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (playlists) {
        is Resource.Loading -> {
            LoadingScreen(modifier = modifier)
        }
        is Resource.Success -> {
            PlaylistList(
                playlists = playlists.data,
                onPlaylistClick = onPlaylistClick,
                onPlaylistMenuClick = onPlaylistMenuClick,
                modifier = modifier
            )
        }
        is Resource.Error -> {
            ErrorScreen(modifier = modifier)
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit,
    onPlaylistMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = playlists, key = { it.id }) { playlist ->
            PlaylistItem(
                onClick = onPlaylistClick,
                playlist = playlist,
                onItemMenuClick = onPlaylistMenuClick
            )
        }
    }
}
