package com.devdunnapps.amplify.ui.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.BottomSheetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistBottomSheet(
    playlist: Playlist,
    onDeletePlaylist: () -> Unit,
    close: () -> Unit
) {
    var isDeletePlaylistDialogVisible by remember { mutableStateOf(false) }

    DeletePlaylistDialog(
        isVisible = isDeletePlaylistDialogVisible,
        onSubmit = {
            isDeletePlaylistDialogVisible = false
            onDeletePlaylist()
        },
        onDismiss = { isDeletePlaylistDialogVisible = false }
    )

    ModalBottomSheet(onDismissRequest = close) {
        Column {
            BottomSheetHeader(title = playlist.title)

            BottomSheetItem(
                icon = Icons.Outlined.Delete,
                text = R.string.delete_playlist,
                onClick = { isDeletePlaylistDialogVisible = true }
            )
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
