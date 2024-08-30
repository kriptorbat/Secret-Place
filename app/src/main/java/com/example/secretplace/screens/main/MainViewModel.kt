package com.example.secretplace.screens.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.secretplace.models.User
import com.example.secretplace.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainViewModel: ViewModel() {
    var auth: FirebaseAuth = Firebase.auth
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER)
    var user = MutableLiveData<User>()
    val listUser = MutableLiveData<MutableList<User>>()

    fun getDataUsers(){
        val value = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val user = ds.getValue(User::class.java)
                        Log.d("usr","user ${user?.login}" )
                        if(user?.email != auth.currentUser?.email) user?.let { users.add(it) }
                    }
                    listUser.value = users
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        databaseReference.addValueEventListener(value)
    }

    fun getUser(){
        val email = auth.currentUser?.email ?: ""
        val value = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val userCurrent = ds.getValue(User::class.java)
                        Log.d("usr","user ${userCurrent?.email}, $email" )
                        if(userCurrent?.email == email) {
                            user.value = userCurrent!!
                            Log.d("AUSUAUSUAUUSUAUU","user ${userCurrent?.email}" )
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        databaseReference.addValueEventListener(value)
    }
    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM",token)
        }
    }
}