package com.devdunnapps.amplify.domain.usecases

import com.devdunnapps.amplify.data.networking.NetworkResponse
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.domain.repository.PlexRepository
import javax.inject.Inject

class GetAlbumSongsUseCase @Inject constructor(
    private val repository: PlexRepository
){

    suspend operator fun invoke(albumId: String): NetworkResponse<List<Song>> = repository.getAlbumSongs(albumId)
}
