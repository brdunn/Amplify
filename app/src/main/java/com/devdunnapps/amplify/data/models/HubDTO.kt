package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HubDTO(
    val type: String,
    val size: Int,
    @SerializedName("Metadata") val metadata: List<MetadataDTO>?,
) : Serializable
