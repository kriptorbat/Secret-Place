package com.example.secretplace.models


data class User(
    var id: String? = "",
    val login: String = "",
    val email: String = "",
    val password: String = "",
    val imageProfileUri: String = ""
)
