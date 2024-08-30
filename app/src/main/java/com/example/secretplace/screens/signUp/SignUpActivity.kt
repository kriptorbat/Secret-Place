package com.example.secretplace.screens.signUp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.secretplace.databinding.ActivitySignUpBinding
import com.example.secretplace.screens.main.MainActivity
import com.example.secretplace.screens.signIn.SignInActivity
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this)[SignUpViewModel::class.java]
    }
    private var pass = true
    private var bitmapProf : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTransparency()
        setListeners()
        setTouchOnEye()
        updateStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && data != null && data.data != null){
            if(resultCode == RESULT_OK){
                binding.imageAdd.visibility = View.GONE
                bitmapProf = uriToBitmap(this,data.data!!)
                binding.imageProfileSignUp.setImageURI(data.data)
            }
        }
    }
    private fun setTransparency(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setListeners(){
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }
        binding.signIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.addImage.setOnClickListener {
            val intentChooser = Intent()
            intentChooser.type = "image/*"
            intentChooser.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intentChooser,1)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchOnEye(){
        binding.edPasswordSignUp.setOnTouchListener { view, event ->
            val DRAWABLE_RIGHT = 2 // Индекс drawableRight в массиве drawables
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edPasswordSignUp.right - binding.edPasswordSignUp.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    if(pass){
                        binding.edPasswordSignUp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                        pass = false
                    } else {
                        binding.edPasswordSignUp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        pass = true
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun signUp(){
        val login = binding.edLoginSignUp.text.toString()
        val email = binding.edEmailSignUp.text.toString()
        val password = binding.edPasswordSignUp.text.toString()

        if(login.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            if(bitmapProf != null){
                binding.buttonSignUp.visibility = View.INVISIBLE
                viewModel.userSignUp(
                    login = login,
                    email = email,
                    password = password,
                    imageProfile = bitmapProf!!)
            } else Toast.makeText(this,"upload image",Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this,"fields email or password is empty",Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus(){
        viewModel.resultAuth.observe(this){
            if(it){
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                binding.buttonSignUp.visibility = View.VISIBLE
                Toast.makeText(this,"Sign Up is Not",Toast.LENGTH_SHORT).show()
            }
        }
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