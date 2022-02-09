package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PartDTO (
    val id: Long?,
    val key: String?,
    val duration: Long?,
    @SerializedName("Stream") val stream: List<StreamDTO>?,
) : Serializable
