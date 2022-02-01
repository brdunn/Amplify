package com.devdunnapps.amplify.data.models

data class SigninDTO(
    val login: String,
    val password: String,
    val verificationCode: String?
)
