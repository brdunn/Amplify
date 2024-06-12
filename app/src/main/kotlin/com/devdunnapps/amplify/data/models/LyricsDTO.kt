package com.devdunnapps.amplify.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LyricsDTO(
    @SerializedName("Line") val lines: List<LineDTO>
) : Serializable {

    fun toRawLyrics(): String {
        var lyrics = ""
        lines.forEach { line ->
            if (line.spans != null) {
                line.spans.forEach { span ->
                    lyrics += span.text + "\n"
                }
            } else {
                lyrics += "\n"
            }
        }
        return lyrics
    }
}
