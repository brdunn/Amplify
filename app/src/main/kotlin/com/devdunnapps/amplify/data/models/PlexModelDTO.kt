package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlexModelDTO (
    @SerializedName("MediaContainer") val mediaContainer: MediaContainerDTO
) : Serializable
