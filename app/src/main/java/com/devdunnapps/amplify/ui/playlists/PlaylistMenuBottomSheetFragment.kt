package com.devdunnapps.amplify.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.BottomSheetItem
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: PlaylistMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    var isDeletePlaylistDialogVisible by remember { mutableStateOf(false) }

                    PlaylistBottomSheet(
                        viewModel = viewModel,
                        onDeletePlaylistClicked = { isDeletePlaylistDialogVisible = true }
                    )

                    DeletePlaylistDialog(
                        isVisible = isDeletePlaylistDialogVisible,
                        onSubmit = {
                            viewModel.deletePlaylist()
                            lifecycleScope.launch {
                                repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    viewModel.closeObservable.collect {
                                        isDeletePlaylistDialogVisible = false
                                        findNavController()
                                            .previousBackStackEntry?.savedStateHandle?.set("refreshData", true)
                                        dismiss()
                                    }
                                }
                            }
                        },
                        onDismiss = { isDeletePlaylistDialogVisible = false }
                    )
                }
            }
        }
}

@Composable
private fun DeletePlaylistDialog(isVisible: Boolean, onSubmit: () -> Unit, onDismiss: () -> Unit) {
    if (!isVisible)
        return

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = R.string.delete_playlist_confirmation_message))
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun PlaylistBottomSheet(viewModel: PlaylistMenuBottomSheetViewModel, onDeletePlaylistClicked: () -> Unit) {
    when(val uiState = viewModel.playlist.collectAsState().value) {
        is Resource.Loading -> LoadingPager()
        is Resource.Error -> ErrorScreen()
        is Resource.Success ->
            PlaylistBottomSheetContent(playlist = uiState.data, onDeletePlaylistClicked = onDeletePlaylistClicked)
    }
}

@Composable
private fun PlaylistBottomSheetContent(playlist: Playlist, onDeletePlaylistClicked: () -> Unit) {
    Column {
        BottomSheetHeader(title = playlist.title)

        BottomSheetItem(
            icon = Icons.Outlined.Delete,
            text = R.string.delete_playlist,
            onClick = onDeletePlaylistClicked
        )
    }
}
