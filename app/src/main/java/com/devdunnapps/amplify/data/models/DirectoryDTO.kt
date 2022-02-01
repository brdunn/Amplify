package com.devdunnapps.amplify.data.models

import com.devdunnapps.amplify.domain.models.LibrarySection
import java.io.Serializable

data class DirectoryDTO(
    val key: String?,
    val type: String?,
    val title: String?
) : Serializable {

    fun toServer() = LibrarySection(
        type = type!!,
        title = title!!,
        key = key!!,
    )
}
