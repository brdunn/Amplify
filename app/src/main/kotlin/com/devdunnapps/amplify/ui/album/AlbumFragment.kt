package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.utils.FragmentSubDestinationScaffold
import com.devdunnapps.amplify.ui.utils.getCurrentSizeClass
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.TimeUtils
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                val screenTitle =
                    (viewModel.album.collectAsState().value as? Resource.Success)?.data?.album?.title.orEmpty()

                FragmentSubDestinationScaffold(screenTitle = screenTitle) { paddingValues ->
                    AlbumRoute(
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
fun AlbumRoute(viewModel: AlbumViewModel, onSongMenuClick: (String) -> Unit, modifier: Modifier = Modifier) {
    AlbumScreen(
        album = viewModel.album.collectAsState().value,
        onSongClick = { viewModel.playSong(it) },
        onSongMenuClick = onSongMenuClick,
        onPlayAlbumClick = { viewModel.playAlbum(WhenToPlay.NOW, false) },
        onShuffleAlbumClick = { viewModel.playAlbum(WhenToPlay.NOW, true) },
        onPlayNextClick = { viewModel.playAlbum(WhenToPlay.NEXT, false) },
        onAddToQueueClick = { viewModel.playAlbum(WhenToPlay.QUEUE, false) },
        modifier = modifier
    )
}

@Composable
private fun AlbumScreen(
    album: Resource<AlbumScreenUIModel>,
    onSongClick: (Song) -> Unit,
    onSongMenuClick: (String) -> Unit,
    onPlayAlbumClick: () -> Unit,
    onShuffleAlbumClick: () -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (album) {
        is Resource.Loading -> LoadingScreen(modifier = modifier)
        is Resource.Success -> AlbumScreenContent(
            model = album.data,
            onSongClick = onSongClick,
            onPlayAlbumClick = onPlayAlbumClick,
            onShuffleAlbumClick = onShuffleAlbumClick,
            onPlayNextClick = onPlayNextClick,
            onAddToQueueClick = onAddToQueueClick,
            onSongMenuClick = onSongMenuClick,
            modifier = modifier
        )
        is Resource.Error -> ErrorScreen(modifier = modifier)
    }
}

@Composable
private fun AlbumScreenContent(
    model: AlbumScreenUIModel,
    onSongClick: (Song) -> Unit,
    onPlayAlbumClick: () -> Unit,
    onShuffleAlbumClick: () -> Unit,
    onPlayNextClick: () -> Unit,
    onSongMenuClick: (String) -> Unit,
    onAddToQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (getCurrentSizeClass() == WindowWidthSizeClass.Compact) {
        LazyColumn(modifier = modifier) {
            item {
                AlbumHeader(
                    model = model,
                    onPlayClicked = onPlayAlbumClick,
                    onShuffleClicked = onShuffleAlbumClick,
                    onPlayNextClick = onPlayNextClick,
                    onAddToQueueClick = onAddToQueueClick
                )
            }

            itemsIndexed(items = model.songs) { index, song ->
                AlbumSong(
                    song = song,
                    albumPos = index + 1,
                    onClick = { onSongClick(song) },
                    onMenuClick = onSongMenuClick
                )
            }

            item {
                AlbumDurationMetadata(model = model)
            }

            item {
                AlbumFooter(album = model.album)
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AlbumHeaderTablet(
                    model = model,
                    onPlayClicked = onPlayAlbumClick,
                    onShuffleClicked = onShuffleAlbumClick,
                    onPlayNextClick = onPlayNextClick,
                    onAddToQueueClick = onAddToQueueClick
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(items = model.songs) { index, song ->
                    AlbumSong(
                        song = song,
                        albumPos = index + 1,
                        onClick = { onSongClick(song) },
                        onMenuClick = onSongMenuClick
                    )
                }

                item {
                    AlbumDurationMetadata(model = model)
                }

                item {
                    AlbumFooter(album = model.album)
                }
            }
        }
    }
}

@Composable
private fun AlbumHeaderTablet(
    model: AlbumScreenUIModel,
    onPlayClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        ArtworkTitle(
            album = model.album,
            onPlayNextClick = onPlayNextClick,
            onAddToQueueClick = onAddToQueueClick
        )

        PlayControls(
            onPlayClicked = onPlayClicked,
            onShuffleClicked = onShuffleClicked
        )
    }
}

@Composable
private fun AlbumHeader(
    model: AlbumScreenUIModel,
    onPlayClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit
) {
    Column {
        ArtworkTitle(
            album = model.album,
            onPlayNextClick = onPlayNextClick,
            onAddToQueueClick = onAddToQueueClick
        )

        PlayControls(
            onPlayClicked = onPlayClicked,
            onShuffleClicked = onShuffleClicked
        )
    }
}

@Composable
private fun ArtworkTitle(
    album: Album,
    modifier: Modifier = Modifier,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val context = LocalContext.current
        val imageUrl = remember { PlexUtils.getInstance(context).addKeyAndAddress(album.thumb) }

        AsyncImage(
            modifier = Modifier
                .width(250.dp)
                .aspectRatio(1F)
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(4.dp)),
            model = imageUrl,
            placeholder = painterResource(R.drawable.ic_album),
            error = painterResource(R.drawable.ic_album),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = album.title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Text(
            text = album.artistName,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 16.dp)
        )

        Text(
            text =  "Album • ${album.year}",
            modifier = Modifier.padding(start = 16.dp),
        )

        Row(
            modifier = Modifier.padding(start = 4.dp)
        ) {
            IconButton(
                onClick = {
                    onPlayNextClick()
                    Toast.makeText(context, "Playing album next", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlaylistPlay,
                    contentDescription = stringResource(R.string.play_next),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    onAddToQueueClick()
                    Toast.makeText(context, "Added to queue", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.QueueMusic,
                    contentDescription = stringResource(R.string.add_to_queue),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PlayControls(onPlayClicked: () -> Unit, onShuffleClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp),
            onClick = onPlayClicked,
        ) {
            Text(text = stringResource(R.string.play))
        }

        Button(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp),
            onClick = onShuffleClicked,
        ) {
            Text(text = stringResource(R.string.shuffle))
        }
    }
}

@Composable
private fun AlbumDurationMetadata(model: AlbumScreenUIModel, modifier: Modifier = Modifier) {
    val resources = LocalContext.current.resources
    Text(
        text = resources.getQuantityString(
            R.plurals.album_track_count,
            model.album.numSongs,
            model.album.numSongs
        ) + " • " + resources.getQuantityString(
            R.plurals.album_duration,
            model.duration,
            model.duration
        ),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun AlbumFooter(album: Album) {
    Column {
        album.studio?.let { studio ->
            Text(
                text = studio,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        ExpandableText(text = album.review)
    }
}

@Composable
private fun AlbumSong(
    song: Song,
    albumPos: Int,
    onClick: () -> Unit,
    onMenuClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val songDuration = TimeUtils.millisecondsToTime(song.duration)

        Box(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxHeight()
                .aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = albumPos.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${song.artistName} • $songDuration",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }

        IconButton(
            onClick = { onMenuClick(song.id) }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAlbumHeader() {
    Mdc3Theme {
        Surface {
            val album = Album(
                artistId = "0",
                artistName = "Billy Joel",
                artistThumb = "",
                id = "0",
                numSongs = 10,
                review = "",
                studio = "Columbia",
                thumb = "",
                title = "The Complete Albums Collection",
                year = "2000"
            )

            ArtworkTitle(
                album = album,
                onPlayNextClick = {},
                onAddToQueueClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAlbumSong() {
    Mdc3Theme {
        Surface {
            val song = Song(
                year = "2021",
                title = "Hotel TV",
                thumb = "",
                id = "",
                artistThumb = "",
                artistName = "Lawrence",
                artistId = "",
                albumId = "",
                albumName = "Hotel TV",
                duration = 217000,
                songUrl = "",
                userRating = 10,
                playCount = 10
            )

            AlbumSong(
                song = song,
                albumPos = 2,
                onClick = {},
                onMenuClick = {}
            )
        }
    }
}
