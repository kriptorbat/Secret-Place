package com.example.secretplace.models

data class Message(
    var senderId: String? = "",
    var receivedId: String? = "",
    var message: String? = "",
    var dateTime: String? = ""
)
