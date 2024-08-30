package com.example.secretplace.screens.profile

import android.graphics.Bitmap
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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileViewModel: ViewModel() {
    var auth: FirebaseAuth = Firebase.auth
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
        Constants.KEY_USER)
    var user = MutableLiveData<User>()
    val storageReference = FirebaseStorage.getInstance().getReference(Constants.KEY_IMAGE_STORAGE)

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }
    fun loadUser(){
        val email = auth.currentUser?.email ?: ""
        val value = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val userCurrent = ds.getValue(User::class.java)
                        Log.d("usr","user ${userCurrent?.email}, $email" )
                        if(userCurrent?.email == email) {
                            user.value = userCurrent!!
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
    fun loginUpdate(newLogin: String){
        databaseReference.orderByChild(Constants.KEY_EMAIL).equalTo(auth.currentUser?.email ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userKey = userSnapshot.key
                        val loginReference = databaseReference.child("$userKey/login")
                        loginReference.setValue(newLogin)
                            .addOnSuccessListener {
                                loadUser()
                            }
                            .addOnFailureListener {
                                Log.d("sex", "error")
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Обработка ошибки запроса к базе данных
                }
            })
    }
    fun profileUpdate(imageProfile: Bitmap){
        val imageRef = storageReference.child(auth.uid.toString())
        imageRef.delete()
            .addOnSuccessListener {
                val baos = ByteArrayOutputStream()
                imageProfile.compress(Bitmap.CompressFormat.JPEG,100,baos)
                val byteArray = baos.toByteArray()
                val up = imageRef.putBytes(byteArray)
                up.continueWithTask { imageRef.downloadUrl }
                    .addOnCompleteListener { t ->
                        val imageProfileUri = t.result.toString()

                        databaseReference.orderByChild(Constants.KEY_EMAIL).equalTo(auth.currentUser?.email ?: "")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val userKey = userSnapshot.key
                                        val loginReference = databaseReference.child("$userKey/imageProfileUri")
                                        loginReference.setValue(imageProfileUri)
                                            .addOnSuccessListener {
                                                loadUser()
                                            }
                                            .addOnFailureListener {
                                                Log.d("loadUser", "error")
                                            }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Обработка ошибки запроса к базе данных
                                }
                            })
                    }
            }
            .addOnFailureListener {
                Log.d("Delete Image","Delete Image Fail")
            }
    }
}