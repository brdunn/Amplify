package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.models.PlexModelDTO
import com.devdunnapps.amplify.data.networking.NetworkResponse
import retrofit2.http.*

interface PlexAPI {

    @GET("library/sections/{section}/all")
    suspend fun getArtists(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int
    ): NetworkResponse<PlexModelDTO>

    @GET("library/sections/{section}/albums")
    suspend fun getAlbums(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int
    ): NetworkResponse<PlexModelDTO>

    @GET("library/sections/{section}/all?type=10&sort=titleSort")
    suspend fun getSongs(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int
    ): NetworkResponse<PlexModelDTO>

    @GET("library/metadata/{key}")
    suspend fun getAlbum(@Path("key") key: String): NetworkResponse<PlexModelDTO>

    @GET("library/metadata/{key}/children")
    suspend fun getAlbumSongs(@Path("key") key: String): NetworkResponse<PlexModelDTO>

    @GET("playlists?playlistType=audio&smart=false")
    suspend fun getPlaylists(@Query("sectionID") section: String): NetworkResponse<PlexModelDTO>

    @GET("playlists/{playlistKey}")
    suspend fun getPlaylist(@Path("playlistKey") playlistKey: String): NetworkResponse<PlexModelDTO>

    @GET("playlists/{key}/items")
    suspend fun getPlaylistSongs(@Path("key") key: String): NetworkResponse<PlexModelDTO>

    @GET("library/sections/{section}/all?format=EP,Single&type=9&resolveTags=1&sort=year:desc,originallyAvailableAt:desc,artist.titleSort:desc,album.titleSort,album.index,album.id,album.originallyAvailableAt")
    suspend fun getArtistSinglesEPs(
        @Path("section") section: String,
        @Query("artist.id") key: String
    ): NetworkResponse<PlexModelDTO>

    @Headers("X-Plex-Container-Size: 1")
    @GET("library/sections/{section}/all?group=title&sort=ratingCount:desc&type=10")
    suspend fun getArtistSongs(
        @Path("section") section: String,
        @Query("artist.id") key: String,
        @Query("limit") number: Int
    ): NetworkResponse<PlexModelDTO>

    @GET("library/sections/{section}/all?format!=EP,Single&type=9&resolveTags=1&sort=year:desc,originallyAvailableAt:desc,artist.titleSort:desc,album.titleSort,album.index,album.id,album.originallyAvailableAt")
    suspend fun getArtistAlbums(
        @Path("section") section: String,
        @Query("artist.id") key: String
    ): NetworkResponse<PlexModelDTO>

    @GET("library/metadata/{key}")
    suspend fun getArtist(@Path("key") key: String): NetworkResponse<PlexModelDTO>

    @PUT("playlists/{playlistKey}/items")
    suspend fun addSongToPlaylist(
        @Path("playlistKey") playlistKey: String,
        @Query("uri") songKeyUri: String
    ): NetworkResponse<PlexModelDTO>

    @DELETE("playlists/{playlistKey}/items/{playlistItemID}")
    suspend fun removeSongFromPlaylist(
        @Path("playlistKey") playlistKey: String,
        @Path("playlistItemID") playlistItemId: String
    ): NetworkResponse<PlexModelDTO>

    @GET("identity")
    suspend fun getServerIdentity(): NetworkResponse<PlexModelDTO>

    @GET("library/metadata/{key}")
    suspend fun getSong(@Path("key") key: String): NetworkResponse<PlexModelDTO>

    @GET("library/streams/{lyricKey}")
    suspend fun getSongLyrics(@Path("lyricKey") lyricKey: String): NetworkResponse<PlexModelDTO>

    @GET("library/sections")
    suspend fun getLibrarySections(): NetworkResponse<PlexModelDTO>

    @DELETE("playlists/{playlistKey}")
    suspend fun deletePlaylist(@Path("playlistKey") playlistKey: String): NetworkResponse<Unit>

    @POST("playlists?smart=false&type=audio&uri=null")
    suspend fun createPlaylist(@Query("title") title: String): NetworkResponse<Unit>

    @GET("hubs/search")
    suspend fun searchLibrary(@Query("query") query: String): NetworkResponse<PlexModelDTO>

    @POST(":/rate?identifier=com.plexapp.plugins.library")
    suspend fun rateSong(@Query("key") key: String, @Query("rating") rating: String): NetworkResponse<Unit>

    @POST(":/scrobble?identifier=com.plexapp.plugins.library")
    suspend fun markSongAsListened(@Query("key") key: String): NetworkResponse<Unit>

    @PUT("playlists/{id}")
    suspend fun editPlaylistMetadata(
        @Path("id") playlistId: String,
        @Query("title") title: String? = null,
        @Query("summary") summary: String? = null
    ): NetworkResponse<Unit>
}
