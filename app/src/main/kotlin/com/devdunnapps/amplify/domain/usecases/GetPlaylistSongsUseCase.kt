package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetPlaylistSongsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(key: String): NetworkResponse<List<Song>> = repository.getPlaylistSongs(key)
}
