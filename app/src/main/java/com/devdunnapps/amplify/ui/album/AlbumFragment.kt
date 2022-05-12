package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentAlbumBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.rememberViewInteropNestedScrollConnection
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.TimeUtils
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        setSystemUI()

         binding.albumCompose.apply {
            setContent {
                Mdc3Theme {
                    Surface(
                        modifier = Modifier.nestedScroll(rememberViewInteropNestedScrollConnection())
                    ) {
                        AlbumPage(
                            viewModel = viewModel,
                            onPlayNextClick = {
                                viewModel.playAlbum(WhenToPlay.NEXT, false)
                            },
                            onAddToQueueClick = {
                                viewModel.playAlbum(WhenToPlay.QUEUE, false)
                            },
                            onSongMenuClick = { songId ->
                                val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songId)
                                findNavController().navigate(action)
                            }
                        )
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.albumToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.albumToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}

@Composable
private fun AlbumPage(
    viewModel: AlbumViewModel,
    onPlayNextClick: () -> Unit,
    onSongMenuClick: (String) -> Unit,
    onAddToQueueClick: () -> Unit
) {
    val album by viewModel.album.observeAsState(Resource.Loading())
    val songs by viewModel.songs.observeAsState(Resource.Loading())

    if (album is Resource.Success && songs is Resource.Success) {
        LazyColumn{
            item {
                AlbumHeader(
                    viewModel = viewModel,
                    album = album.data!!,
                    onPlayNextClick = onPlayNextClick,
                    onAddToQueueClick = onAddToQueueClick
                )
            }

            itemsIndexed(
                items = songs.data!!
            ) { index, song ->
                AlbumSong(
                    song = song,
                    albumPos = index + 1,
                    onClick = { viewModel.playSong(song) },
                    onMenuClick = onSongMenuClick
                )
            }

            item {
                AlbumFooter(album = album.data!!)
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
private fun AlbumHeader(
    viewModel: AlbumViewModel,
    album: Album,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit
) {
    Column {
        ArtworkTitle(
            album = album,
            onPlayNextClick = onPlayNextClick,
            onAddToQueueClick = onAddToQueueClick
        )

        PlayControls(
            onPlayClicked = { viewModel.playAlbum(WhenToPlay.NOW, false) },
            onShuffleClicked = { viewModel.playAlbum(WhenToPlay.NOW, true) }
        )

        AlbumDurationMetadata(
            viewModel = viewModel,
            album = album
        )
    }
}

@Composable
private fun ArtworkTitle(album: Album, onPlayNextClick: () -> Unit, onAddToQueueClick: () -> Unit) {
    Row {
        val context = LocalContext.current
        val imageUrl = remember { PlexUtils.getInstance(context).addKeyAndAddress(album.thumb) }
        Image(
            modifier = Modifier
                .padding(16.dp)
                .weight(1F)
                .aspectRatio(1F)
                .clip(shape = RoundedCornerShape(4.dp)),
            painter = rememberImagePainter(
                data = imageUrl,
                imageLoader = LocalImageLoader.current,
                builder = {
                    placeholder(R.drawable.ic_albums_black_24dp)
                    error(R.drawable.ic_albums_black_24dp)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .weight(1F)
                .aspectRatio(1F, false)
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                val maxTextStyle = TextStyle (
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
                var textStyle by remember { mutableStateOf(maxTextStyle) }
                var readyToDraw by remember { mutableStateOf(false) }

                Text(
                    text = album.title,
                    style = textStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    modifier = Modifier
                        .drawWithContent {
                            if (readyToDraw) drawContent()
                        }
                        .padding(start = 16.dp),
                    onTextLayout = { result ->
                        if (result.didOverflowHeight || result.isLineEllipsized(0)) {
                            textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                        } else {
                            readyToDraw = true
                        }
                    }
                )
            }

            Text(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = buildAnnotatedString {
                    append("by ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(album.artistName)
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                maxLines = 1,
                text =  "Album • ${album.year}"
            )

            Row(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(start = 4.dp)
            ) {
                IconButton(
                    onClick = { onPlayNextClick() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlaylistPlay,
                        contentDescription = "Play next"
                    )
                }

                IconButton(
                    onClick = { onAddToQueueClick() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QueueMusic,
                        contentDescription = "Add to queue"
                    )
                }
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
            Text(text = "Play")
        }

        Button(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp),
            onClick = onShuffleClicked,
        ) {
            Text(text = "Shuffle")
        }
    }
}

@Composable
private fun AlbumDurationMetadata(viewModel: AlbumViewModel, album: Album) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val resources = LocalContext.current.resources
        Text(
            text = resources.getQuantityString(R.plurals.album_track_count, album.numSongs, album.numSongs)
        )

        val albumDuration by viewModel.albumDuration.observeAsState()
        albumDuration?.let {
            val duration = TimeUtils.millisecondsToMinutes(it)
            Text(
                text = resources.getQuantityString(R.plurals.album_duration, duration, duration)
            )
        }
    }
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
