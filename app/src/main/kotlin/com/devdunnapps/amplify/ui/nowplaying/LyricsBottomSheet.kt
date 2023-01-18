package com.devdunnapps.amplify.ui.nowplaying

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingPager
import com.devdunnapps.amplify.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LyricsBottomSheet(
    songId: String,
    title: String,
    subtitle: String,
    viewModel: LyricsBottomSheetViewModel = hiltViewModel<
            LyricsBottomSheetViewModel,
            LyricsBottomSheetViewModel.LyricsBottomSheetViewModelFactory
    > { factory ->
        factory.create(songId)
    },
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column {
            BottomSheetHeader(title = title, subtitle = subtitle)

            LazyColumn {
                item { LyricsContent(lyrics = viewModel.songLyrics.collectAsState().value) }
            }
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
