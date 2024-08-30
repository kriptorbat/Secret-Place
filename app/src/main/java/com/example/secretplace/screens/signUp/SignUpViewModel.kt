package com.example.secretplace.screens.signUp

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.secretplace.models.User
import com.example.secretplace.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SignUpViewModel: ViewModel() {
    var auth: FirebaseAuth = Firebase.auth
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
        Constants.KEY_USER)
    val storageReference = FirebaseStorage.getInstance().getReference(Constants.KEY_IMAGE_STORAGE)
    var resultAuth = MutableLiveData<Boolean>()

    fun userSignUp(login: String,email: String, password: String,imageProfile: Bitmap){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val id = auth.uid
                uploadProfile(
                    id = id,
                    login = login,
                    email = email,
                    password = password,
                    imageProfile = imageProfile
                )
            } else resultAuth.value = false
        }
    }

    private fun uploadProfile(id: String?,login: String,email: String,password: String,imageProfile: Bitmap){
        val baos = ByteArrayOutputStream()
        imageProfile.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val byteArray = baos.toByteArray()
        val mRef = storageReference.child(auth.uid.toString())
        val up = mRef.putBytes(byteArray)
        val task = up.continueWithTask { mRef.downloadUrl }
            .addOnCompleteListener { t ->
                val uploadUri = t.result
                Log.d("ss",uploadUri.toString())
                val saveUser = User(
                    id = id,
                    login = login,
                    email = email,
                    password = password,
                    imageProfileUri = t.result.toString()
                )
                databaseReference.push().setValue(saveUser).addOnCompleteListener {
                    resultAuth.value = true
                }
            }
    }


}