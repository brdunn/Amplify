package com.devdunnapps.amplify.ui.nowplaying

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.usecases.GetAlbumSongsUseCase
import com.devdunnapps.amplify.domain.usecases.GetAlbumUseCase
import com.devdunnapps.amplify.domain.usecases.GetSongLyricsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LyricsBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSongLyricsUseCase: GetSongLyricsUseCase
) : ViewModel() {

    private val songId: String = savedStateHandle["songId"]!!

    val songLyrics: LiveData<Resource<Lyric>> = getSongLyricsUseCase(songId).asLiveData()
}
