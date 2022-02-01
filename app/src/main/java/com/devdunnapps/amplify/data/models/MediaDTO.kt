package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MediaDTO  (
    @SerializedName("Part") val part: List<PartDTO>?
) : Serializable
