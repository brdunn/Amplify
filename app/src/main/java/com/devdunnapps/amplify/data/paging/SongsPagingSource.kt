package com.devdunnapps.amplify.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devdunnapps.amplify.data.PlexAPI
import com.devdunnapps.amplify.domain.models.Song

private const val STARTING_KEY = 0

class SongsPagingSource (
    private val section: String,
    private val api: PlexAPI
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val startKey = params.key ?: STARTING_KEY

        try {
            val response = api.getSongs(
                section = section,
                containerSize = params.loadSize,
                containerStart = startKey
            )
            val songs = response.mediaContainer.metadata?.map { it.toSong() } ?: emptyList()

            return LoadResult.Page(
                data = songs,
                prevKey = (startKey - params.loadSize).takeIf { it > STARTING_KEY },
                nextKey = response.mediaContainer.totalSize?.let { totalSize ->
                    (startKey + params.loadSize).takeIf { it < totalSize }
                }
            )
        } catch(e: Exception) {
            return LoadResult.Error(Exception("error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? = null
}
