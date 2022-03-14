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
fun AlbumHeader(album: Album) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
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
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                val textStyleBody1 = MaterialTheme.typography.displayLarge
                var textStyle by remember { mutableStateOf(textStyleBody1) }
                var readyToDraw by remember { mutableStateOf(false) }

                Text(
                    text = album.title,
                    style = textStyle,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.drawWithContent {
                        if (readyToDraw) drawContent()
                    },
                    onTextLayout = { result ->
                        if (result.didOverflowHeight) {
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
fun PlayControls(onPlayClicked: () -> Unit, onShuffleClicked: () -> Unit) {
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
fun AlbumPage(viewModel: AlbumViewModel, onSongMenuClick: (String) -> Unit) {
    Column(

    ) {
        val album by viewModel.album.observeAsState()
        val songs by viewModel.songs.observeAsState()

        if (album is Resource.Success && songs is Resource.Success) {
            // TODO: ew
            val successAlbumData = album!!.data!!
            val successSongsData = songs!!.data!!

            LazyColumn{
                item {
                    AlbumHeader(album = successAlbumData)

                    PlayControls(
                        onPlayClicked = { viewModel.playAlbum(WhenToPlay.NOW, false) },
                        onShuffleClicked = { viewModel.playAlbum(WhenToPlay.NOW, true) }
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        val resources = LocalContext.current.resources
                        Text(
                            text = resources.getQuantityString(R.plurals.album_track_count, successAlbumData.numSongs, successAlbumData.numSongs)
                        )

                        val albumDuration by viewModel.albumDuration.observeAsState()
                        albumDuration?.let {
                            val duration = TimeUtils.millisecondsToMinutes(albumDuration!!)
                            Text(
                                text = resources.getQuantityString(R.plurals.album_duration, duration, duration)
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = successSongsData
                ) { index, song ->
                    AlbumSong(
                        song = song,
                        albumPos = index + 1,
                        onClick = { viewModel.playSong(song) },
                        onMenuClick = onSongMenuClick
                    )
                }

                item {
                    Text(
                        text = successAlbumData.studio,
                        modifier = Modifier.padding(16.dp)
                    )

                    ExpandableText(
                        text = successAlbumData.review
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumSong(
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
                .padding(vertical = 4.dp, horizontal = 16.dp)
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
