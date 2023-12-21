package com.devdunnapps.amplify.domain.repository

import androidx.paging.PagingSource
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.models.Lyric
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.domain.models.SearchResults
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlexRepository {

    fun getArtists(): PagingSource<Int, Artist>

    fun getAlbums(): PagingSource<Int, Album>

    fun getSongs(): PagingSource<Int, Song>

    suspend fun getSong(songId: String): NetworkResponse<Song>

    suspend fun getAlbum(key: String): NetworkResponse<Album>

    suspend fun getAlbumSongs(key: String): NetworkResponse<List<Song>>

    suspend fun getPlaylists(): NetworkResponse<List<Playlist>>

    suspend fun getPlaylist(playlistId: String): NetworkResponse<Playlist>

    suspend fun getPlaylistSongs(key: String): NetworkResponse<List<Song>>

    suspend fun getArtistSongs(artistKey: String, number: Int = -1): NetworkResponse<List<Song>>

    suspend fun getArtistSinglesEPs(artistKey: String): NetworkResponse<List<Album>>

    suspend fun getArtistAlbums(key: String): NetworkResponse<List<Album>>

    suspend fun getArtist(key: String): NetworkResponse<Artist>

    suspend fun addSongToPlaylist(songId: String, playlistId: String): Boolean

    suspend fun removeSongFromPlaylist(songId: String, playlistId: String): Boolean

    fun getSongLyrics(songId: String): Flow<Resource<Lyric>>

    suspend fun getLibrarySections(): NetworkResponse<List<LibrarySection>>

    suspend fun deletePlaylist(playlistId: String): NetworkResponse<Unit>

    suspend fun createPlaylist(playlistTitle: String): NetworkResponse<Unit>

    fun searchLibrary(query: String): Flow<Resource<SearchResults>>

    suspend fun rateSong(songId: String, rating: Int): NetworkResponse<Unit>

    suspend fun markSongAsListened(songId: String): NetworkResponse<Unit>

    suspend fun editPlaylistMetadata(playlistId: String, title: String?, summary: String?): NetworkResponse<Unit>

    suspend fun getRecentlyPlayedArtists(): NetworkResponse<List<Artist>>

    suspend fun getRecentlyAddedAlbums(): NetworkResponse<List<Album>>

    suspend fun getRecentlyPlayedSongs() : NetworkResponse<List<Song>>
}
