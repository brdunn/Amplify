package com.devdunnapps.amplify.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devdunnapps.amplify.data.PlexAPI
import com.devdunnapps.amplify.domain.models.Album

private const val STARTING_KEY = 0

class AlbumsPagingSource (
    private val section: String,
    private val api: PlexAPI
) : PagingSource<Int, Album>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val startKey = params.key ?: STARTING_KEY

        try {
            val response = api.getAlbums(
                section = section,
                containerSize = params.loadSize,
                containerStart = startKey
            )
            val albums = response.mediaContainer.metadata?.map { it.toAlbum() } ?: emptyList()

            return LoadResult.Page(
                data = albums,
                prevKey = (startKey - params.loadSize).takeIf { it > STARTING_KEY },
                nextKey = response.mediaContainer.totalSize?.let { totalSize ->
                    (startKey + params.loadSize).takeIf { it < totalSize }
                }
            )
        } catch(e: Exception) {
            return LoadResult.Error(Exception("error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null
}
