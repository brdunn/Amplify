package com.devdunnapps.amplify.ui.album

import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Song

data class AlbumScreenUIModel (
    val album: Album,
    val songs: List<Song>,
    val duration: Int
)
