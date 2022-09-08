package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.paging.AlbumsPagingSource
import com.devdunnapps.amplify.data.paging.ArtistsPagingSource
import com.devdunnapps.amplify.data.paging.SongsPagingSource
import com.devdunnapps.amplify.domain.models.*
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Named

class PlexRepositoryImpl @Inject constructor(
    private val api: PlexAPI,
    @Named("plexToken") private val userToken: String,
    @Named("library") private val section: String
) : PlexRepository {

    override fun getArtists() = ArtistsPagingSource(userToken, section, api)

    override fun getAlbums() = AlbumsPagingSource(userToken, section, api)

    override fun getSongs() = SongsPagingSource(userToken, section, api)

    override fun getSong(songId: String): Flow<Resource<Song>> = flow {
        emit(Resource.Loading())
        try {
            val song = api.getSong(songId, userToken).mediaContainer.metadata?.get(0)?.toSong()
            emit(Resource.Success(song))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load song, please check your internet connection."))
        }
    }

    override fun getAlbum(key: String): Flow<Resource<Album>> = flow {
        emit(Resource.Loading())
        try {
            val album = api.getAlbum(key, userToken).mediaContainer.metadata?.get(0)?.toAlbum()
            emit(Resource.Success(album))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load album, please check your internet connection."))
        }
    }

    override fun getAlbumSongs(key: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading())
        try {
            val songs = api.getAlbumSongs(key, userToken).mediaContainer.metadata?.map { it.toSong() } ?: emptyList()
            emit(Resource.Success(songs))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load album songs, please check your internet connection."))
        }
    }

    override fun getPlaylists(): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading())
        try {
            val playlists =
                api.getPlaylists(section, userToken).mediaContainer.metadata?.map { it.toPlaylist() } ?: emptyList()
            emit(Resource.Success(playlists))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load playlists, please check your internet connection."))
        }
    }

    override fun getPlaylist(playlistId: String): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            val playlist = api.getPlaylist(playlistId, userToken).mediaContainer.metadata?.get(0)?.toPlaylist()
            emit(Resource.Success(playlist))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load playlist, please check your internet connection."))
        }
    }

    override fun getPlaylistSongs(key: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading())
        try {
            val songs = api.getPlaylistSongs(key, userToken).mediaContainer.metadata?.map { it.toSong() } ?: emptyList()
            emit(Resource.Success(songs))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load playlist songs, please check your internet connection."))
        }
    }

    override fun getArtistSongs(artistKey: String): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading())
        try {
            val metadata = api.getArtistSongs(section, artistKey, userToken).mediaContainer.metadata
            if (metadata == null) {
                emit(Resource.Error("Artist with specified ID does not exist"))
                return@flow
            }
            emit(Resource.Success(metadata.map { it.toSong() }))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load artist's song, please check your internet connection."))
        }
    }

    override fun getArtistSinglesEPs(artistKey: String) = flow {
        emit(Resource.Loading())
        try {
            val singlesEPs = api.getArtistSinglesEPs(section, artistKey, userToken).mediaContainer.metadata
                ?.map { it.toAlbum() } ?: emptyList()
            emit(Resource.Success(singlesEPs))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load artist's EPS and singles, please check your internet connection."))
        }
    }

    override fun getArtistAlbums(key: String): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())
        try {
            val albums = api.getArtistAlbums(section, key, userToken).mediaContainer.metadata
                ?.map { it.toAlbum() } ?: emptyList()
            emit(Resource.Success(albums))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't get artist albums, please check your internet connection."))
        }
    }

    override fun getArtist(key: String): Flow<Resource<Artist>> = flow {
        emit(Resource.Loading())
        try {
            val metadata = api.getArtist(key, userToken).mediaContainer.metadata
            if (metadata == null) {
                emit(Resource.Error("Artist with specified ID does not exist"))
                return@flow
            }
            emit(Resource.Success(metadata[0].toArtist()))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't get artist, please check your internet connection."))
        }
    }

    override fun addSongToPlaylist(songId: String, playlistId: String): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            val serverId = api.getServerIdentity(userToken).mediaContainer.machineIdentifier!!
            val songUri = "server://$serverId/com.plexapp.plugins.library/library/metadata/$songId"
            api.addSongToPlaylist(playlistId, songUri, userToken)
            emit(Resource.Success())
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't add song to playlist, please check your internet connection."))
        }
    }

    override fun removeSongFromPlaylist(songId: String, playlistId: String): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            // Plex uses a unique "playlistItemID" so we must search the playlist for the playlistItemID of the song with the given songId
            val playlistSongs = api.getPlaylistSongs(playlistId, userToken).mediaContainer.metadata
            val playlistItemId = playlistSongs?.find { it.ratingKey == songId }?.playlistItemID

            if (playlistItemId == null) {
                emit(Resource.Error("Could not remove item from specified playlist"))
                return@flow
            }

            api.removeSongFromPlaylist(playlistId, playlistItemId, userToken)
            emit(Resource.Success())
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't remove song from playlist, please check your internet connection."))
        }
    }

    override fun getSongLyrics(songId: String): Flow<Resource<Lyric>> = flow {
        emit(Resource.Loading())
        try {
            val streams = api.getSong(songId, userToken).mediaContainer.metadata!![0].media!![0].part!![0].stream!!
            for (stream in streams) {
                val isTextLyric = stream.streamType == 4 && stream.format.equals("txt")
                if (isTextLyric) {
                    // TODO: fix blocking call
                    val rawLyrics = api.getSongLyrics(stream.id!!, userToken).string()
                    val lyrics = Lyric(songId, rawLyrics)
                    emit(Resource.Success(lyrics))
                }
            }
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't get song lyrics, please check your internet connection."))
        }
    }

    override fun deletePlaylist(playlistId: String): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            val apiResponse = api.deletePlaylist(playlistId, userToken)
            if (apiResponse.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                emit(Resource.Success())
            } else {
                emit(Resource.Error("Error deleting playlist"))
            }
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't delete playlist, please check your internet connection."))
        }
    }

    override fun createPlaylist(playlistTitle: String): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            val apiResponse = api.createPlaylist(playlistTitle, userToken).mediaContainer.metadata?.get(0)?.toPlaylist()
            emit(Resource.Success(apiResponse))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't create playlist, please check your internet connection."))
        }
    }

    override fun searchLibrary(query: String): Flow<Resource<SearchResults>> = flow {
        emit(Resource.Loading())
        try {
            var songs: List<Song> = emptyList()
            var albums: List<Album> = emptyList()
            var artists: List<Artist> = emptyList()
            var playlists: List<Playlist> = emptyList()

            val metadata = api.searchLibrary(query, userToken).mediaContainer.hub!!
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
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't find anything, please check your internet connection."))
        }
    }

    override fun rateSong(songId: String, rating: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val apiResponse = api.rateSong(songId, rating.toString(), userToken)
            if (apiResponse.code() == HttpURLConnection.HTTP_OK) {
                emit(Resource.Success())
            } else {
                emit(Resource.Error("Error rating song"))
            }
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't rate song, please check your internet connection."))
        }
    }

    override fun markSongAsListened(songId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val apiResponse = api.markSongAsListened(songId, userToken)
            if (apiResponse.code() == HttpURLConnection.HTTP_OK) {
                emit(Resource.Success())
            } else {
                emit(Resource.Error("Could not mark as listened"))
            }
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't mark as listened, please check your internet connection."))
        }
    }
}
