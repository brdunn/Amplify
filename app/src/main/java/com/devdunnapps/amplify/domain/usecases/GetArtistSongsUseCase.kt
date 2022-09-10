package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistSongsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    operator fun invoke(artistKey: String, number: Int = -1): Flow<Resource<List<Song>>> =
        repository.getArtistSongs(artistKey, number)
}
