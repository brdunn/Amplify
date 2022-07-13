package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun `Get Song Successfully`() = runTest {
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
    fun `Get Album Songs Successfully`() = runTest {
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
    fun `Get Playlist Successfully`() = runTest {
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
    fun `Get Playlist Songs Successfully`() = runTest {
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
    fun `Get Artist Songs Successfully`() = runTest {
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
    fun `Get Artist Successfully`() = runTest {
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
    fun `Add Song to Playlist Successfully`() = runTest {
        repository.addSongToPlaylist(TEST_SONG_ID, TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Remove Song From Playlist Successfully`() = runTest {
        repository.removeSongFromPlaylist("44562", TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Delete Playlist Successfully`() = runTest {
        repository.deletePlaylist(TEST_PLAYLIST_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            }
            // TODO: add more tests
        }
    }

    @Test
    fun `Create Playlist Successfully`() = runTest {
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
    fun `Search Library Successfully`() = runTest {
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
    fun `Rate Song Successfully`() = runTest {
        repository.rateSong(TEST_SONG_ID, 10).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
            }
        }
    }

    @Test
    fun `Listen to Song Successfully`() = runTest {
        repository.markSongAsListened(TEST_SONG_ID).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
            }
        }
    }
}
