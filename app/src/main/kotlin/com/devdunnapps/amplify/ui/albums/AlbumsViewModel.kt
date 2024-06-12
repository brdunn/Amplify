package com.devdunnapps.amplify.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.domain.repository.PlexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val PAGE_SIZE = 50

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val plexRepository: PlexRepository
) : ViewModel() {

    val albums: Flow<PagingData<Album>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { plexRepository.getAlbums() }
    ).flow.cachedIn(viewModelScope)
}
