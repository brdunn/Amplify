package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.models.PlexModelDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PlexAPI {

    @GET("library/sections/{section}/all")
    suspend fun getArtists(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/sections/{section}/albums")
    suspend fun getAlbums(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/sections/{section}/all?type=10&sort=titleSort")
    suspend fun getSongs(
        @Path("section") section: String,
        @Query("X-Plex-Container-Size") containerSize: Int,
        @Query("X-Plex-Container-Start") containerStart: Int,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/metadata/{key}")
    suspend fun getAlbum(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/metadata/{key}/children")
    suspend fun getAlbumSongs(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("playlists?playlistType=audio&smart=false")
    suspend fun getPlaylists(
        @Query("sectionID") section: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("playlists/{playlistKey}")
    suspend fun getPlaylist(
        @Path("playlistKey") playlistKey: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("playlists/{key}/items")
    suspend fun getPlaylistSongs(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @Headers("X-Plex-Container-Size: 1")
    @GET("library/sections/{section}/all?album.subformat!=Compilation,Live&type=9")
    suspend fun getArtistSinglesEPs(
        @Path("section") section: String,
        @Query("artist.id") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @Headers("X-Plex-Container-Size: 1")
    @GET("library/sections/{section}/all?group=title&sort=ratingCount:desc&type=10")
    suspend fun getArtistSongs(
        @Path("section") section: String,
        @Query("artist.id") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/metadata/{key}/children")
    suspend fun getArtistAlbums(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/metadata/{key}")
    suspend fun getArtist(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @PUT("playlists/{playlistKey}/items")
    suspend fun addSongToPlaylist(
        @Path("playlistKey") playlistKey: String,
        @Query("uri") songKeyUri: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @DELETE("playlists/{playlistKey}/items/{playlistItemID}")
    suspend fun removeSongFromPlaylist(
        @Path("playlistKey") playlistKey: String,
        @Path("playlistItemID") playlistItemId: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("identity")
    suspend fun getServerIdentity(
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/metadata/{key}")
    suspend fun getSong(
        @Path("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("library/streams/{lyricKey}")
    suspend fun getSongLyrics(
        @Path("lyricKey") lyricKey: String,
        @Header("X-Plex-Token") userToken: String
    ): ResponseBody

    @GET("library/sections")
    suspend fun getLibrarySections(
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @DELETE("playlists/{playlistKey}")
    suspend fun deletePlaylist(
        @Path("playlistKey") playlistKey: String,
        @Header("X-Plex-Token") userToken: String
    ): Response<Unit>

    @POST("playlists?smart=false&type=audio&uri=null")
    suspend fun createPlaylist(
        @Query("title") title: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @GET("hubs/search")
    suspend fun searchLibrary(
        @Query("query") query: String,
        @Header("X-Plex-Token") userToken: String
    ): PlexModelDTO

    @POST(":/rate?identifier=com.plexapp.plugins.library")
    suspend fun rateSong(
        @Query("key") key: String,
        @Query("rating") rating: String,
        @Header("X-Plex-Token") userToken: String
    ): Response<Unit>

    @POST(":/scrobble?identifier=com.plexapp.plugins.library")
    suspend fun markSongAsListened(
        @Query("key") key: String,
        @Header("X-Plex-Token") userToken: String
    ): Response<Unit>
}
