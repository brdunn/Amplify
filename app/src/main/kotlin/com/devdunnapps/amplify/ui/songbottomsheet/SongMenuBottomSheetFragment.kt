package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueuePlayNext
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Rating
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.BottomSheetItem
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: SongMenuBottomSheetViewModel by viewModels()
    private val navArgs: SongMenuBottomSheetFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    SongBottomSheet(
                        viewModel = viewModel,
                        onPlayNextClick = {
                            viewModel.playSong(WhenToPlay.NEXT)
                            dismiss()
                        },
                        onAddToQueueClick = {
                            viewModel.playSong(WhenToPlay.QUEUE)
                            dismiss()
                        },
                        onGoToAlbumClick = { albumId ->
                            (activity as MainActivity).bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albumId)
                            findNavController().navigate(action)
                            dismiss()
                        },
                        onGoToArtistClick = { artistId ->
                            (activity as MainActivity).bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            val action = MobileNavigationDirections.actionGlobalNavigationArtist(artistId)
                            findNavController().navigate(action)
                            dismiss()
                        },
                        onAddToPlaylist = { songId ->
                            val action = SongMenuBottomSheetFragmentDirections
                                .actionNavigationSongBottomSheetToAddToPlaylistBottomSheet(songId)
                            findNavController().navigate(action)
                        },
                        onRemoveFromPlaylist = navArgs.playlistId?.let { viewModel::removeSongFromPlaylist },
                        onLyricsClick = { song ->
                            val action = SongMenuBottomSheetFragmentDirections
                                .actionNavigationSongBottomSheetToSongLyrics(song)
                            findNavController().navigate(action)
                        },
                        onInfoClick = { song ->
                            val action = SongMenuBottomSheetFragmentDirections
                                .actionNavigationSongBottomSheetToSongAdditionalInfoBottomSheetFragment(song)
                            findNavController().navigate(action)
                        },
                        refreshPreviousScreen = {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("refreshData", true)
                            dismiss()
                        }
                    )
                }
            }
        }
}

@Composable
private fun SongBottomSheet(
    viewModel: SongMenuBottomSheetViewModel,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onGoToAlbumClick: (String) -> Unit,
    onGoToArtistClick: (String) -> Unit,
    onAddToPlaylist: (String) -> Unit,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onLyricsClick: (Song) -> Unit,
    onInfoClick: (Song) -> Unit,
    refreshPreviousScreen: () -> Unit
) {

    when (val screenState = viewModel.screenState.collectAsState().value) {
        is Resource.Loading -> LoadingPager()
        is Resource.Error -> ErrorScreen()
        is Resource.Success -> SongBottomSheetContent(
            state = screenState.data,
            onPlayNextClick = onPlayNextClick,
            onAddToQueueClick = onAddToQueueClick,
            onGoToAlbumClick = onGoToAlbumClick,
            onGoToArtistClick = onGoToArtistClick,
            onAddToPlaylist = onAddToPlaylist,
            onRemoveFromPlaylist = onRemoveFromPlaylist,
            onLyricsClick = onLyricsClick,
            onInfoClick = onInfoClick,
            onRatingClick = viewModel::rateSong,
            refreshPreviousScreen = refreshPreviousScreen
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
    onLyricsClick: (Song) -> Unit,
    onInfoClick: (Song) -> Unit,
    onRatingClick: (Int) -> Unit,
    refreshPreviousScreen: () -> Unit
) {
    LaunchedEffect(key1 = state) {
        if (state.refreshPreviousScreen)
            refreshPreviousScreen()
    }

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
                    icon = Icons.Outlined.Lyrics,
                    text = R.string.lyrics,
                    onClick = { onLyricsClick(song) }
                )
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
