package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetArtistSongsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(artistKey: String, number: Int = -1): NetworkResponse<List<Song>> =
        repository.getArtistSongs(artistKey, number)
}
