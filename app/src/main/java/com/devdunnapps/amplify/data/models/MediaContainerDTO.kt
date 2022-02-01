package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MediaContainerDTO(
    @SerializedName("Metadata") val metadata: List<MetadataDTO>?,
    @SerializedName("Directory") val directory: List<DirectoryDTO>?,
    @SerializedName("Hub") val hub: List<HubDTO>?,
    val art: String?,
    val thumb: String?,
    val parentTitle: String?,
    val summary: String?,
    val size: Int?,
    val grandparentTitle: String?,
    val parentYear: String?,
    val title: String?,
    val key: String?,
    val machineIdentifier: String?
) : Serializable
