package com.devdunnapps.amplify.ui.songbottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddToQueue
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueuePlayNext
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Rating
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.BottomSheetItem
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay

@Composable
internal fun SongBottomSheet(
    viewModel: SongMenuBottomSheetViewModel = hiltViewModel(),
    close: () -> Unit,
    onGoToAlbumClick: (String) -> Unit,
    onGoToArtistClick: (String) -> Unit,
    onAddToPlaylist: (String) -> Unit,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onInfoClick: (Song) -> Unit
) {
    when (val screenState = viewModel.screenState.collectAsState().value) {
        is Resource.Loading -> LoadingPager()
        is Resource.Error -> ErrorScreen()
        is Resource.Success -> SongBottomSheetContent(
            state = screenState.data,
            onPlayNextClick = {
                viewModel.playSong(WhenToPlay.NEXT)
                close()
            },
            onAddToQueueClick = {
                viewModel.playSong(WhenToPlay.QUEUE)
                close()
            },
            onGoToAlbumClick = onGoToAlbumClick,
            onGoToArtistClick = onGoToArtistClick,
            onAddToPlaylist = onAddToPlaylist,
            onRemoveFromPlaylist = onRemoveFromPlaylist,
            onInfoClick = onInfoClick,
            onRatingClick = viewModel::rateSong
        )
    }
}

@Composable
private fun SongBottomSheetContent(
    state: SongBottomSheetState,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onGoToAlbumClick: (String) -> Unit,
    onGoToArtistClick: (String) -> Unit,
    onAddToPlaylist: (String) -> Unit,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onInfoClick: (Song) -> Unit,
    onRatingClick: (Int) -> Unit
) {
    val song = state.song

    Column {
        BottomSheetHeader(
            title = song.title,
            subtitle = song.artistName,
            image = PlexUtils.getInstance(LocalContext.current).addKeyAndAddress(song.thumb),
            additionalContent = {
                UserRating(rating = song.userRating, onRatingClick = onRatingClick)
            }
        )

        LazyColumn {
            item {
                BottomSheetItem(
                    icon = Icons.Outlined.QueuePlayNext,
                    text = R.string.play_next,
                    onClick = onPlayNextClick
                )
            }

            item {
                BottomSheetItem(
                    icon = Icons.Outlined.AddToQueue,
                    text = R.string.add_to_queue,
                    onClick = onAddToQueueClick
                )
            }

            item {
                BottomSheetItem(
                    icon = Icons.Outlined.Album,
                    text = R.string.go_to_album,
                    onClick = { onGoToAlbumClick(song.albumId) }
                )
            }

            item {
                BottomSheetItem(
                    icon = Icons.Outlined.Person,
                    text = R.string.go_to_artist,
                    onClick = { onGoToArtistClick(song.artistId) }
                )
            }

            item {
                BottomSheetItem(
                    icon = Icons.Outlined.Add,
                    text = R.string.add_to_playlist,
                    onClick = { onAddToPlaylist(song.id) }
                )
            }

            onRemoveFromPlaylist?.let {
                item {
                    BottomSheetItem(
                        icon = Icons.Outlined.Delete,
                        text = R.string.remove_from_playlist,
                        onClick = it
                    )
                }
            }

            item {
                BottomSheetItem(
                    icon = Icons.Outlined.Info,
                    text = R.string.info,
                    onClick = { onInfoClick(song) }
                )
            }
        }
    }
}

@Composable
private fun UserRating(rating: Int, onRatingClick: (Int) -> Unit) {
    Row {
        IconButton(onClick = { onRatingClick(Rating.THUMB_DOWN) }) {
            Icon(
                imageVector = if (rating == Rating.THUMB_DOWN) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                contentDescription = null
            )
        }

        IconButton(onClick = { onRatingClick(Rating.THUMB_UP)}) {
            Icon(
                imageVector = if (rating == Rating.THUMB_UP) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = null
            )
        }
    }
}
