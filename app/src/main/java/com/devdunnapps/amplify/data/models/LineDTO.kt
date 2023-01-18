package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LineDTO(
    @SerializedName("Span") val spans: List<SpanDTO>?
) : Serializable
