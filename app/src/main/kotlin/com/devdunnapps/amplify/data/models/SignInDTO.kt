package com.devdunnapps.amplify.data.models

data class SignInDTO(
    val login: String,
    val password: String,
    val verificationCode: String?
)
