package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlexRepositoryImplTest {

    private lateinit var repository: PlexRepository

    @Before
    fun setUp() {
        val api = Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(FakePlexAPI()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.google.com")  // This value must be a valid URL but it is not used
            .build()
            .create(PlexAPI::class.java)

        repository = PlexRepositoryImpl(
            api = api,
            section = TEST_LIBRARY_ID,
            userToken = ""
        )
    }

    @Test
    fun `Get Artists Successfully`() = runBlocking {
        repository.getArtists().collectIndexed { index, response ->
            if (index == 1) {
                assert(response is Resource.Success)
                val artists = response.data!!
                assert(artists.size == 4)
                assert(artists[0].name == "10,000 Maniacs")
            }
        }
    }

    @Test
    fun `Get Albums Successfully`() = runBlocking {
        repository.getAlbums().collectIndexed { index, response ->
            if (index == 1) {
                assert(response is Resource.Success)
                val albums = response.data!!
                assert(albums.size == 3)
                assert(albums[0].title == "8 Mile: Music From and Inspired by the Motion Picture")
            }
        }
    }

    @Test
    fun `Get Songs Successfully`() = runBlocking {
        repository.getSongs().collectIndexed { index, value ->
            if (index == 1) {
                assert(value is Resource.Success)

                val songs = value.data!!
                assert(songs.size == 2)
                assert(songs[0].title == "The (After) Life of the Party")
            }
        }
    }

    @Test
    fun `Get Song Successfully`() = runBlocking {
        repository.getSong(TEST_SONG_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1 ) {
                assert(response is Resource.Success)
                val song = response.data!!
                assert(song.id == TEST_SONG_ID)
                assert(song.title == "The Night We Met")
            }
        }
    }

    @Test
    fun `Get Album Songs Successfully`() = runBlocking {
        repository.getAlbumSongs(TEST_ALBUM_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val songs = response.data!!
                assert(songs.size == 14)
                assert(songs[0].title == "These Are Days")
                assert(songs[0].artistName == "10,000 Maniacs")
            }
        }
    }

    @Test
    fun `Get Playlists Successfully`() = runBlocking {
        repository.getPlaylists().collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val playlists = response.data!!
                assert(playlists.size == 3)
                assert(playlists[0].id == "46993")
                assert(playlists[0].numSongs == 16)
            }
        }
    }

    @Test
    fun `Get Playlist Successfully`() = runBlocking {
        repository.getPlaylist(TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val playlist = response.data!!
                assert(playlist.title == "Bops")
                assert(playlist.numSongs == 16)
            }
        }
    }

    @Test
    fun `Get Playlist Songs Successfully`() = runBlocking {
        repository.getPlaylistSongs(TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val songs = response.data!!
                assert(songs.size == 16)
                assert(songs[0].title == "What Life Would Be Like")
            }
        }
    }

    @Test
    fun `Get Artist Songs Successfully`() = runBlocking {
        repository.getArtistSongs(TEST_ARTIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val songs = response.data!!
                assert(songs.size == 20)
                assert(songs[0].title == "Like the Weather")
            }
        }
    }

    @Test
    fun `Get Artist Successfully`() = runBlocking {
        repository.getArtist(TEST_ARTIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val artist = response.data!!
                assert(artist.id == TEST_ARTIST_ID)
                assert(artist.name == "10,000 Maniacs")
            }
        }
    }

    @Test
    fun `Add Song to Playlist Successfully`() = runBlocking {
        repository.addSongToPlaylist(TEST_SONG_ID, TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Remove Song From Playlist Successfully`() = runBlocking {
        repository.removeSongFromPlaylist("44562", TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Delete Playlist Successfully`() = runBlocking {
        repository.deletePlaylist(TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Create Playlist Successfully`() = runBlocking {
        repository.createPlaylist(TEST_PLAYLIST_TITLE).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val playlist = response.data!!
                assert(playlist.title == TEST_PLAYLIST_TITLE)
                assert(playlist.numSongs == 0)
            }
        }
    }

    @Test
    fun `Search Library Successfully`() = runBlocking {
        repository.searchLibrary(TEST_SEARCH_LIBRARY_QUERY).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val searchResults = response.data!!
                assert(searchResults.songs.size == 3)
                assert(searchResults.artists.size == 3)
                assert(searchResults.albums.size == 3)
            }
        }
    }

    @Test
    fun `Rate Song Successfully`() = runBlocking {
        repository.rateSong(TEST_SONG_ID, 10).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
            }
        }
    }

    @Test
    fun `Listen to Song Successfully`() = runBlocking {
        repository.markSongAsListened(TEST_SONG_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
            }
        }
    }
}
