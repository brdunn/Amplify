package com.devdunnapps.amplify.ui.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.navArgs
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LyricsBottomSheet : BottomSheetDialogFragment() {

    private val args: LyricsBottomSheetArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    LyricsBottomSheet(title = args.song.title, subtitle = args.song.artistName)
                }
            }
        }
}

@Composable
private fun LyricsBottomSheet(
    title: String,
    subtitle: String,
    viewModel: LyricsBottomSheetViewModel = hiltViewModel()
) {
    Column {
        BottomSheetHeader(title, subtitle)

        LazyColumn {
            item { LyricsContent(lyrics = viewModel.songLyrics.collectAsState().value) }
        }
    }
}

@Composable
private fun LyricsContent(lyrics: Resource<Lyric>) {
    when (lyrics) {
        is Resource.Success -> Text(text = lyrics.data.lyrics, modifier = Modifier.padding(16.dp))
        is Resource.Error -> ErrorScreen()
        is Resource.Loading -> LoadingPager()
    }
}
