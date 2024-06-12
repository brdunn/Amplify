package com.devdunnapps.amplify.ui.nowplaying

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.domain.usecases.GetSongLyricsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LyricsBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSongLyricsUseCase: GetSongLyricsUseCase
) : ViewModel() {

    private val songId = LyricsBottomSheetArgs.fromSavedStateHandle(savedStateHandle).song.id

    private val _songLyrics = MutableStateFlow<Resource<Lyric>>(Resource.Loading)
    val songLyrics = _songLyrics.asStateFlow()

    init {
        getSongLyrics()
    }

    private fun getSongLyrics() {
        viewModelScope.launch {
            getSongLyricsUseCase(songId).collect {
                _songLyrics.emit(it)
            }
        }
    }
}
