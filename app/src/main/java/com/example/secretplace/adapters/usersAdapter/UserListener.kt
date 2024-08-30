package com.example.secretplace.adapters.usersAdapter

import com.example.secretplace.models.User

interface UserListener {
    fun onUserClicked(user: User)
}