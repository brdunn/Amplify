package com.devdunnapps.amplify.ui.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.PlaylistItem
import com.devdunnapps.amplify.ui.components.RootDestinationAppBar
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.delay

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

    LaunchedEffect(Unit) {
        delay(timeMillis = 100)
        focusRequester.requestFocus()
    }
}

@Composable
internal fun PlaylistsRoute(
    onPlaylistClick: (String) -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    PlaylistsScreen(
        playlists = viewModel.playlists.collectAsState().value,
        topBarActions = topBarActions,
        onCreatePlaylistClick = viewModel::createPlaylist,
        deletePlaylist = viewModel::deletePlaylist,
        onPlaylistClick = onPlaylistClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistsScreen(
    playlists: Resource<List<Playlist>>,
    topBarActions: @Composable RowScope.() -> Unit,
    onCreatePlaylistClick: (String) -> Unit,
    deletePlaylist: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isCreatePlaylistDialogVisible by rememberSaveable { mutableStateOf(false) }
    var openedPlaylistBottomSheetId by rememberSaveable { mutableStateOf<Playlist?>(null) }

    if (isCreatePlaylistDialogVisible) {
        CreatePlaylistDialog(
            onSubmit = {
                onCreatePlaylistClick(it)
                isCreatePlaylistDialogVisible = false
            },
            onDismiss = { isCreatePlaylistDialogVisible = false }
        )
    }

    openedPlaylistBottomSheetId?.let {
        PlaylistBottomSheet(
            playlist = it,
            onDeletePlaylist = {
                deletePlaylist(it.id)
                openedPlaylistBottomSheetId = null
            },
            close = { openedPlaylistBottomSheetId = null }
        )
    }

    Scaffold(
        topBar = {
            RootDestinationAppBar(
                title = stringResource(id = R.string.playlists_tab_title),
                actions = topBarActions,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (playlists is Resource.Success) {
                FloatingActionButton(onClick = { isCreatePlaylistDialogVisible = true }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
        ) {
            when (playlists) {
                is Resource.Loading -> {
                    LoadingScreen(modifier = modifier)
                }

                is Resource.Success -> {
                    PlaylistList(
                        playlists = playlists.data,
                        onPlaylistClick = onPlaylistClick,
                        onPlaylistMenuClick = { openedPlaylistBottomSheetId = it },
                        modifier = modifier
                    )
                }

                is Resource.Error -> {
                    ErrorScreen(modifier = modifier)
                }
            }
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit,
    onPlaylistMenuClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = playlists, key = { it.id }) { playlist ->
            PlaylistItem(
                onClick = onPlaylistClick,
                playlist = playlist,
                onItemMenuClick = onPlaylistMenuClick
            )
        }
    }
}
