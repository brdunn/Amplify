package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.navArgs
import com.devdunnapps.amplify.R
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongAdditionalInfoBottomSheetFragment: BottomSheetDialogFragment() {

    private val args: SongAdditionalInfoBottomSheetFragmentArgs by navArgs()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    val playCount = args.song.playCount

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = pluralStringResource(R.plurals.song_play_count, playCount, playCount),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
}
