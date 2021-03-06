package com.devdunnapps.amplify.domain.repository

import androidx.paging.PagingSource
import com.devdunnapps.amplify.domain.models.*
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlexRepository {

    fun getArtists(): PagingSource<Int, Artist>

    fun getAlbums(): PagingSource<Int, Album>

    fun getSongs(): PagingSource<Int, Song>

    fun getSong(songId: String): Flow<Resource<Song>>

    fun getAlbum(key: String): Flow<Resource<Album>>

    fun getAlbumSongs(key: String): Flow<Resource<List<Song>>>

    fun getPlaylists(): Flow<Resource<List<Playlist>>>

    fun getPlaylist(playlistId: String): Flow<Resource<Playlist>>

    fun getPlaylistSongs(key: String): Flow<Resource<List<Song>>>

    fun getArtistSongs(artistKey: String): Flow<Resource<List<Song>>>

    fun getArtistSinglesEPs(artistKey: String): Flow<Resource<List<Album>>>

    fun getArtistAlbums(key: String): Flow<Resource<List<Album>>>

    fun getArtist(key: String): Flow<Resource<Artist>>

    fun addSongToPlaylist(songId: String, playlistId: String): Flow<Resource<Playlist>>

    fun removeSongFromPlaylist(songId: String, playlistId: String): Flow<Resource<Playlist>>

    fun getSongLyrics(songId: String): Flow<Resource<Lyric>>

    fun deletePlaylist(playlistId: String): Flow<Resource<Playlist>>

    fun createPlaylist(playlistTitle: String): Flow<Resource<Playlist>>

    fun searchLibrary(query: String): Flow<Resource<SearchResults>>

    fun rateSong(songId: String, rating: Int): Flow<Resource<Unit>>

    fun markSongAsListened(songId: String): Flow<Resource<Unit>>
}
