package com.example.secretplace.screens.signIn

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.secretplace.databinding.ActivitySignInBinding
import com.example.secretplace.screens.main.MainActivity
import com.example.secretplace.screens.signUp.SignUpActivity

class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by lazy {
        ViewModelProvider(this)[SignInViewModel::class.java]
    }
    private var pass = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTransparency()
        setListeners()
        updateStatus()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.getCurUser()
        if(currentUser != null) startActivity(Intent(this, MainActivity::class.java))
    }
    private fun setTransparency(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(){
        binding.buttonSignIn.setOnClickListener {
            signIn()
        }
        binding.signIn.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.edPasswordSignIn.setOnTouchListener { view, event ->
            val DRAWABLE_RIGHT = 2 // Индекс drawableRight в массиве drawables
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edPasswordSignIn.right - binding.edPasswordSignIn.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    if(pass){
                        binding.edPasswordSignIn.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                        pass = false
                    } else {
                        binding.edPasswordSignIn.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        pass = true
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
    private fun signIn(){
        val email = binding.edEmailSignIn.text.toString()
        val password = binding.edPasswordSignIn.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            viewModel.signIn(email = email, password = password)
        } else {
            Toast.makeText(this,"Field login or email is empty ",Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateStatus(){
        viewModel.resultAuth.observe(this){
            if(it) startActivity(Intent(this, MainActivity::class.java))
            else Toast.makeText(this,"poshol nahui",Toast.LENGTH_SHORT).show()
        }
    }
}