package com.example.secretplace.screens.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.secretplace.databinding.ActivityProfileBinding
import com.example.secretplace.models.User
import com.example.secretplace.screens.signIn.SignInActivity
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }
    var user = MutableLiveData<User>()
    private var bitmapProf : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        loadUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && data != null && data.data != null){
            if(resultCode == RESULT_OK){
                bitmapProf = uriToBitmap(this,data.data!!)
                if(bitmapProf != null){
                    viewModel.profileUpdate(imageProfile = bitmapProf!!)
                } else Toast.makeText(this,"upload image",Toast.LENGTH_SHORT).show()
                binding.imageProfile.setImageURI(data.data)
            }
        }
    }

    private fun setListeners(){
        binding.imageProfile.setOnClickListener {
            setNewImageProfile()
        }
        binding.textUser.setOnClickListener {
            visibleEdText(true)
        }
        binding.buttonPutLogin.setOnClickListener {
            loginUpdate()
        }
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
    private fun loadUser(){
        viewModel.loadUser()
        viewModel.user.observe(this){
            Picasso.get().load(it.imageProfileUri).into(binding.imageProfile)
            binding.textUser.text = it.login
        }
    }
    private fun visibleEdText(visible: Boolean){
        if(visible){
            binding.editLogin.visibility = View.VISIBLE
            binding.textUser.visibility = View.GONE
        } else {
            binding.editLogin.visibility = View.GONE
            binding.textUser.visibility = View.VISIBLE
        }

    }
    private fun signOut(){
        viewModel.signOut()
        startActivity(Intent(this,SignInActivity::class.java))
        Toast.makeText(this,"Sign out is succesful",Toast.LENGTH_SHORT).show()
    }
    private fun loginUpdate(){
        val newLogin = binding.edNewLogin.text.toString()
        if(newLogin != "") {
            viewModel.loginUpdate(newLogin)
            visibleEdText(false)
        } else Toast.makeText(this,"Login is empty",Toast.LENGTH_SHORT).show()
    }
    private fun setNewImageProfile(){
        val intentChooser = Intent()
        intentChooser.type = "image/*"
        intentChooser.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentChooser,1)
    }
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}