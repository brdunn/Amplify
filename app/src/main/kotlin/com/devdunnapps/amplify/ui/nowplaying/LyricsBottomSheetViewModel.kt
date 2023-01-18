package com.devdunnapps.amplify.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.domain.usecases.GetSongLyricsUseCase
import com.devdunnapps.amplify.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LyricsBottomSheetViewModel.LyricsBottomSheetViewModelFactory::class)
class LyricsBottomSheetViewModel @AssistedInject constructor(
    @Assisted private val songId: String,
    private val getSongLyricsUseCase: GetSongLyricsUseCase
) : ViewModel() {
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

    @AssistedFactory
    interface LyricsBottomSheetViewModelFactory {
        fun create(songId: String): LyricsBottomSheetViewModel
    }
}
