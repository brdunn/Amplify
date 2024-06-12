package com.devdunnapps.amplify.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devdunnapps.amplify.data.PlexAPI
import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Artist

private const val STARTING_KEY = 0

class ArtistsPagingSource (
    private val section: String,
    private val api: PlexAPI
) : PagingSource<Int, Artist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val startKey = params.key ?: STARTING_KEY

        val response = api.getArtists(
            section = section,
            containerSize = params.loadSize,
            containerStart = startKey
        )

        if (response is NetworkResponse.Failure)
            return LoadResult.Error(Exception(response.code.toString()))

        val artists = response.data.mediaContainer.metadata?.map { it.toArtist() } ?: emptyList()

        return LoadResult.Page(
            data = artists,
            prevKey = (startKey - params.loadSize).takeIf { it > STARTING_KEY },
            nextKey = response.data.mediaContainer.totalSize?.let { totalSize ->
                (startKey + params.loadSize).takeIf { it < totalSize }
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? = null
}
