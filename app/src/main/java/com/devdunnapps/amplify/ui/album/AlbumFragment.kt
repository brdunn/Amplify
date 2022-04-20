package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.view.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_album, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_album_add_to_queue -> {
                viewModel.playAlbum(WhenToPlay.QUEUE)
                true
            }
            R.id.menu_album_play_next -> {
                viewModel.playAlbum(WhenToPlay.NEXT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        setHasOptionsMenu(true)
    }
}

@Composable
private fun AlbumPage(viewModel: AlbumViewModel, onSongMenuClick: (String) -> Unit) {
    val album by viewModel.album.observeAsState(Resource.Loading())
    val songs by viewModel.songs.observeAsState(Resource.Loading())

    if (album is Resource.Success && songs is Resource.Success) {
        LazyColumn{
            item {
                AlbumHeader(
                    viewModel = viewModel,
                    album = album.data!!
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
private fun AlbumHeader(viewModel: AlbumViewModel, album: Album) {
    Column {
        ArtworkTitle(album = album)

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
private fun ArtworkTitle(album: Album) {
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
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                val maxTextStyle = MaterialTheme.typography.displayLarge
                var textStyle by remember { mutableStateOf(maxTextStyle) }
                var readyToDraw by remember { mutableStateOf(false) }

                Text(
                    text = album.title,
                    style = textStyle,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .drawWithContent {
                            if (readyToDraw) drawContent()
                        },
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
                    .weight(1F)
                    .fillMaxSize(),
                text = album.artistName + " • " + album.year
            )
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
        Text(
            text = album.studio,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() }
    ) {
        val (artwork, title, artist, menu) = createRefs()
        val guideline = createGuidelineFromTop(0.5f)

        Box(
            modifier = Modifier
                .constrainAs(artwork) {
                    start.linkTo(parent.start)
                }
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

        Text(
            text = song.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    bottom.linkTo(guideline)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = song.artistName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(artist) {
                    start.linkTo(artwork.end)
                    end.linkTo(menu.start)
                    top.linkTo(guideline)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Start
        )

        IconButton(
            onClick = { onMenuClick(song.id) },
            modifier = Modifier
                .constrainAs(menu) {
                    end.linkTo(parent.end)
                }
                .fillMaxHeight()
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
fun PreviewAlbumHeader() {
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
                title = "Antisocialites",
                year = "2000"
            )

            val songs = listOf(
                Song(
                    year = "2000",
                    title = "Summer Highland Falls",
                    thumb = "",
                    id = "",
                    artistThumb = "",
                    artistName = "Billy Joel",
                    artistId = "",
                    albumId = "",
                    albumName = "Complete Albums Collection",
                    duration = 56000,
                    songUrl = "",
                    userRating = 10,
                    playCount = 10
                ),
                Song(
                    year = "2000",
                    title = "Summer Highland Falls",
                    thumb = "",
                    id = "",
                    artistThumb = "",
                    artistName = "Billy Joel",
                    artistId = "",
                    albumId = "",
                    albumName = "Complete Albums Collection",
                    duration = 56000,
                    songUrl = "",
                    userRating = 10,
                    playCount = 10
                ),
                Song(
                    year = "2000",
                    title = "Summer Highland Falls",
                    thumb = "",
                    id = "",
                    artistThumb = "",
                    artistName = "Billy Joel",
                    artistId = "",
                    albumId = "",
                    albumName = "Complete Albums Collection",
                    duration = 56000,
                    songUrl = "",
                    userRating = 10,
                    playCount = 10
                )
            )

            ArtworkTitle(album = album)
        }
    }
}
