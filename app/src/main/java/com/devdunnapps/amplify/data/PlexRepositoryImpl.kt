package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.data.networking.map
import com.devdunnapps.amplify.data.paging.AlbumsPagingSource
import com.devdunnapps.amplify.data.paging.ArtistsPagingSource
import com.devdunnapps.amplify.data.paging.SongsPagingSource
import com.devdunnapps.amplify.domain.models.*
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class PlexRepositoryImpl @Inject constructor(
    private val api: PlexAPI,
    @Named("library") private val section: String
) : PlexRepository {

    override fun getArtists() = ArtistsPagingSource(section, api)

    override fun getAlbums() = AlbumsPagingSource(section, api)

    override fun getSongs() = SongsPagingSource(section, api)

    override suspend fun getSong(songId: String): NetworkResponse<Song> =
        api.getSong(songId).map { it.mediaContainer.metadata?.get(0)?.toSong() }

    override suspend fun getAlbum(key: String): NetworkResponse<Album> =
        api.getAlbum(key).map { it.mediaContainer.metadata?.get(0)?.toAlbum() }

    override suspend fun getAlbumSongs(key: String): NetworkResponse<List<Song>> =
        api.getAlbumSongs(key).map {
            it.mediaContainer.metadata?.map { metadataDTO -> metadataDTO.toSong() } ?: emptyList()
        }

    override suspend fun getPlaylists(): NetworkResponse<List<Playlist>> =
        api.getPlaylists(section).map {
            it.mediaContainer.metadata?.map { metadataDTO ->  metadataDTO.toPlaylist() } ?: emptyList()
        }

    override suspend fun getPlaylist(playlistId: String): NetworkResponse<Playlist> =
        api.getPlaylist(playlistId).map { it.mediaContainer.metadata?.get(0)?.toPlaylist() }

    override suspend fun getPlaylistSongs(key: String): NetworkResponse<List<Song>> =
        api.getPlaylistSongs(key).map {
            it.mediaContainer.metadata?.map { metadataDTO -> metadataDTO.toSong() } ?: emptyList()
        }

    override suspend fun getArtistSongs(artistKey: String, number: Int): NetworkResponse<List<Song>> =
        api.getArtistSongs(section, artistKey, number).map {
            it.mediaContainer.metadata?.map { metadata -> metadata.toSong() } ?: emptyList()
        }

    override suspend fun getArtistSinglesEPs(artistKey: String): NetworkResponse<List<Album>> =
        api.getArtistSinglesEPs(section, artistKey)
            .map { it.mediaContainer.metadata?.map { metadata -> metadata.toAlbum() } ?: emptyList() }

    override suspend fun getArtistAlbums(key: String): NetworkResponse<List<Album>> =
        api.getArtistAlbums(section, key)
            .map { it.mediaContainer.metadata?.map { metadata -> metadata.toAlbum() } ?: emptyList() }

    override suspend fun getArtist(key: String): NetworkResponse<Artist> =
        api.getArtist(key).map { it.mediaContainer.metadata?.get(0)?.toArtist() }

    override suspend fun addSongToPlaylist(songId: String, playlistId: String): Boolean {
        val serverIdentityResult = api.getServerIdentity().map { it.mediaContainer.machineIdentifier }

        if (serverIdentityResult is NetworkResponse.Failure) return false

        val serverId = serverIdentityResult.data
        val songUri = "server://$serverId/com.plexapp.plugins.library/library/metadata/$songId"

        return api.addSongToPlaylist(playlistId, songUri) is NetworkResponse.Success
    }

    override suspend fun removeSongFromPlaylist(songId: String, playlistId: String): Boolean {
        // Plex uses a unique "playlistItemID" so we must search the playlist for the playlistItemID of the song
        // with the given songId
        val playlistSongsResponse = (api.getPlaylistSongs(playlistId) as? NetworkResponse.Success)?.data ?: return false
        val playlistItemId = playlistSongsResponse.mediaContainer.metadata?.find {
            it.ratingKey == songId
        }?.playlistItemID ?: return false

        return api.removeSongFromPlaylist(playlistId, playlistItemId) is NetworkResponse.Success
    }

    override fun getSongLyrics(songId: String): Flow<Resource<Lyric>> = flow {
        emit(Resource.Loading)

        val songResult = api.getSong(songId)
        if (songResult is NetworkResponse.Failure) {
            emit(Resource.Error())
            return@flow
        }

        val streams = songResult.data.mediaContainer.metadata?.get(0)?.media?.get(0)?.part?.get(0)?.stream
        if (streams == null) {
            emit(Resource.Error())
            return@flow
        }

        for (stream in streams) {
            if (stream.streamType == 4 && stream.id != null) {
                val rawLyricsResult =
                    api.getSongLyrics(stream.id).map { it.mediaContainer.lyrics?.get(0)?.toRawLyrics() }

                if (rawLyricsResult is NetworkResponse.Failure) {
                    emit(Resource.Error())
                    return@flow
                }

                val lyrics = Lyric(songId, rawLyricsResult.data)
                emit(Resource.Success(lyrics))
                return@flow
            }
        }

        emit(Resource.Error())
    }

    override suspend fun getLibrarySections(): NetworkResponse<List<LibrarySection>> =
        api.getLibrarySections().map {
            it.mediaContainer.directory
                ?.filter { directory -> directory.type == "artist" }
                ?.map { directory -> directory.toLibrarySection() }
                ?: emptyList()
        }

    override suspend fun deletePlaylist(playlistId: String) =
        api.deletePlaylist(playlistId)

    override suspend fun createPlaylist(playlistTitle: String): NetworkResponse<Unit> =
        api.createPlaylist(playlistTitle)

    override fun searchLibrary(query: String): Flow<Resource<SearchResults>> = flow {
        emit(Resource.Loading)
        var songs: List<Song> = emptyList()
        var albums: List<Album> = emptyList()
        var artists: List<Artist> = emptyList()
        var playlists: List<Playlist> = emptyList()

        val searchResult = api.searchLibrary(query).map { it.mediaContainer.hub }

        if (searchResult is NetworkResponse.Failure) {
            emit(Resource.Error())
            return@flow
        }

        val metadata = searchResult.data
        val songsMetadata = metadata
            .first { it.type == "track" }
            .metadata
        if (songsMetadata != null) {
            songs = songsMetadata
                .filter { it.librarySectionID == section }
                .map { it.toSong() }
        }

        val albumsMetadata = metadata
            .first { it.type == "album" }
            .metadata
        if (albumsMetadata != null) {
            albums = albumsMetadata
                .filter { it.librarySectionID == section}
                .map { it.toAlbum() }
        }

        val artistsMetadata = metadata
            .first { it.type == "artist" }
            .metadata
        if (artistsMetadata != null) {
            artists = artistsMetadata
                .filter { it.librarySectionID == section}
                .map { it.toArtist() }
        }

        // TODO: does this pull from other libraries?
        val playlistsMetadata = metadata
            .first { it.type == "playlist" }
            .metadata
        if (playlistsMetadata != null) {
            playlists = playlistsMetadata.map { it.toPlaylist() }
        }

        emit(Resource.Success(SearchResults(songs, albums, artists, playlists)))
    }

    override suspend fun rateSong(songId: String, rating: Int): NetworkResponse<Unit> =
        api.rateSong(songId, rating.toString())

    override suspend fun markSongAsListened(songId: String): NetworkResponse<Unit> =
        api.markSongAsListened(songId)

    override suspend fun editPlaylistMetadata(
        playlistId: String,
        title: String?,
        summary: String?
    ): NetworkResponse<Unit> =
        api.editPlaylistMetadata(playlistId = playlistId, title = title, summary = summary)
}
