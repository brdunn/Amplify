package com.devdunnapps.amplify.utils

object StringUtils {

    fun toTitleCase(input: String): String {
        val titleCase = StringBuilder(input.length)
        var nextTitleCase = true
        var letter: Char
        for (c in input) {
            letter = c
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true
            } else if (nextTitleCase) {
                letter = Character.toUpperCase(c)
                nextTitleCase = false
            }
            titleCase.append(letter)
        }
        return titleCase.toString()
    }
}
