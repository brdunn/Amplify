package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.data.networking.NetworkResponseAdapterFactory
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
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.google.com")  // This value must be a valid URL but it is not used
            .build()
            .create(PlexAPI::class.java)

        repository = PlexRepositoryImpl(api = api, section = TEST_LIBRARY_ID)
    }

    @Test
    fun `Get Song Successfully`() = runTest {
        val response = repository.getSong(TEST_SONG_ID)
        assert(response is NetworkResponse.Success)
        val song = response.data
        assert(song.id == TEST_SONG_ID)
        assert(song.title == "The Night We Met")

    }

    @Test
    fun `Get Album Songs Successfully`() = runTest {
        val response = repository.getAlbumSongs(TEST_ALBUM_ID)
        assert(response is NetworkResponse.Success)
        val songs = response.data
        assert(songs.size == 14)
        assert(songs[0].title == "These Are Days")
        assert(songs[0].artistName == "10,000 Maniacs")

    }

    @Test
    fun `Get Playlist Successfully`() = runTest {
        val response = repository.getPlaylist(TEST_PLAYLIST_ID)
            assert(response is NetworkResponse.Success)
            val playlist = response.data
            assert(playlist.title == "Bops")
            assert(playlist.numSongs == 16)
    }

    @Test
    fun `Get Playlist Songs Successfully`() = runTest {
        val response = repository.getPlaylistSongs(TEST_PLAYLIST_ID)
        assert(response is NetworkResponse.Success)
        val songs = response.data
        assert(songs.size == 16)
        assert(songs[0].title == "What Life Would Be Like")
    }

    @Test
    fun `Get Artist Songs Successfully`() = runTest {
        val response = repository.getArtistSongs(TEST_ARTIST_ID)
        assert(response is NetworkResponse.Success)
        val songs = response.data
        assert(songs.size == 20)
        assert(songs[0].title == "Like the Weather")
    }

    @Test
    fun `Get Artist Successfully`() = runTest {
        val response = repository.getArtist(TEST_ARTIST_ID)
        assert(response is NetworkResponse.Success)
        val artist = response.data
        assert(artist.id == TEST_ARTIST_ID)
        assert(artist.name == "10,000 Maniacs")
    }

    @Test
    fun `Delete Playlist Successfully`() = runTest {
        val response = repository.deletePlaylist(TEST_PLAYLIST_ID)
        assert(response is NetworkResponse.Success)
    }

    @Test
    fun `Create Playlist Successfully`() = runTest {
        val response = repository.createPlaylist(TEST_PLAYLIST_TITLE)
        assert(response is NetworkResponse.Success)
    }

    @Test
    fun `Search Library Successfully`() = runTest {
        repository.searchLibrary(TEST_SEARCH_LIBRARY_QUERY).collectIndexed { index, response ->
            if (index == 0) {
                assert(response is Resource.Loading)
            } else if (index == 1) {
                assert(response is Resource.Success)
                val searchResults = (response as Resource.Success).data
                assert(searchResults.songs.size == 3)
                assert(searchResults.artists.size == 3)
                assert(searchResults.albums.size == 3)
            }
        }
    }

    @Test
    fun `Rate Song Successfully`() = runTest {
        val response = repository.rateSong(TEST_SONG_ID, 10)
        assert(response is NetworkResponse.Success)
    }

    @Test
    fun `Listen to Song Successfully`() = runTest {
        assert(repository.markSongAsListened(TEST_SONG_ID) is NetworkResponse.Success)
    }
}
