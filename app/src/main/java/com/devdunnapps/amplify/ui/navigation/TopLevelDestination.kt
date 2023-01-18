package com.devdunnapps.amplify.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.devdunnapps.amplify.R

enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconText: Int
) {
    ARTISTS(
        selectedIcon = R.drawable.ic_artists,
        unselectedIcon = R.drawable.ic_artists_outlined,
        iconText = R.string.artists
    ),

    ALBUMS(
        selectedIcon = R.drawable.ic_album,
        unselectedIcon = R.drawable.ic_album_outlined,
        iconText = R.string.albums
    ),

    SONGS(
        selectedIcon = R.drawable.ic_songs,
        unselectedIcon = R.drawable.ic_songs_outlined,
        iconText = R.string.songs
    ),

    PLAYLISTS(
        selectedIcon = R.drawable.ic_playlists,
        unselectedIcon = R.drawable.ic_playlists_outlined,
        iconText = R.string.playlists
    )
}
