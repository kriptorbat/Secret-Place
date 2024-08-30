package com.example.secretplace.screens.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel: ViewModel(){
    var auth: FirebaseAuth = Firebase.auth
    var resultAuth = MutableLiveData<Boolean>()

    fun getCurUser() = auth.currentUser

    fun signIn(email: String,password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            resultAuth.value = task.isSuccessful
        }
    }
}