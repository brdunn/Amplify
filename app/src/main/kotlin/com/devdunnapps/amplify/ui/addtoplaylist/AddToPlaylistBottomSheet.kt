package com.devdunnapps.amplify.ui.addtoplaylist

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource

@Composable
internal fun AddToPlaylistBottomSheet(
    viewModel: AddToPlaylistViewModel = hiltViewModel(),
    close: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.closeObservable.collect {
            close()
        }
    }

    Column(
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        Text(
            text = stringResource(R.string.add_to_playlist),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )

        when (val playlists = viewModel.playlists.collectAsState().value) {
            is Resource.Loading -> LoadingPager()

            is Resource.Error ->
                Toast.makeText(LocalContext.current, "Error getting playlists", Toast.LENGTH_SHORT)
                    .show()

            is Resource.Success -> BottomSheetList(
                playlists = playlists.data,
                onItemClick = { playlist -> viewModel.addSongToPlaylist(playlist.id) }
            )
        }
    }
}

@Composable
private fun BottomSheetList(
    playlists: List<Playlist>,
    onItemClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        playlists.forEach { playlist ->
            PlaylistItem(playlist = playlist, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onItemClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(playlist) }
            .padding(8.dp)
    ) {
        Text(
            text = playlist.title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = LocalContext.current.resources.getQuantityString(
                R.plurals.album_track_count,
                playlist.numSongs,
                playlist.numSongs
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
