package com.example.secretplace.screens.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.secretplace.adapters.usersAdapter.UsersAdapter
import com.example.secretplace.adapters.usersAdapter.UserListener
import com.example.secretplace.databinding.ActivityMainBinding
import com.example.secretplace.models.User
import com.example.secretplace.screens.chat.ChatActivity
import com.example.secretplace.screens.profile.ProfileActivity
import com.example.secretplace.utilities.Constants
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), UserListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getToken()

        setListeners()
        loadUser()
        loadUsers()
    }

    private fun setListeners(){
        binding.imageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    private fun loadUser(){
        viewModel.getUser()
        viewModel.user.observe(this){
            Picasso.get().load(it.imageProfileUri).into(binding.imageProfile)
            binding.textUser.text = it.login
        }
    }
    private fun loadUsers(){
        viewModel.getDataUsers()
        viewModel.listUser.observe(this){
            val usersAdapter = UsersAdapter(it,this)
            binding.usersRecycler.adapter = usersAdapter
        }
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_LOGIN,user.login)
        intent.putExtra(Constants.KEY_ID,user.id)
        startActivity(intent)
    }
}