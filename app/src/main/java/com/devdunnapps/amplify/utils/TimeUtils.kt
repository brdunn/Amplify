package com.devdunnapps.amplify.utils

object TimeUtils {

    fun millisecondsToTime(milliseconds : Long) : String {
        val minutes = milliseconds / 60000
        val seconds = ((milliseconds / 1000) % 60)
            .toString()
            .take(2)
            .padStart(2, '0')
        return "$minutes:$seconds"
    }

    fun millisecondsToMinutes(milliseconds : Long) = (milliseconds / 60000).toInt()
}
