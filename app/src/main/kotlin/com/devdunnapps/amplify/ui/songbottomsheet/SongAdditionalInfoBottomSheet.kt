package com.devdunnapps.amplify.ui.songbottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.components.BottomSheetHeader
import com.devdunnapps.amplify.utils.PlexUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongAdditionalInfoBottomSheet(
    title: String,
    thumb: String,
    playCount: Int
) {
    Column {
        BottomSheetHeader(
            title = title,
            subtitle = stringResource(R.string.additional_information),
            image = PlexUtils.getInstance(LocalContext.current).addKeyAndAddress(thumb)
        )
        Text(
            text = pluralStringResource(R.plurals.song_play_count, playCount, playCount),
            modifier = Modifier.padding(8.dp)
        )
    }
}
