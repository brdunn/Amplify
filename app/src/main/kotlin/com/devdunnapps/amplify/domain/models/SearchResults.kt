package com.devdunnapps.amplify.domain.models

data class SearchResults(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>,
    val playlists: List<Playlist>
)
