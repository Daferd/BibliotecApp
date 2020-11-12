package com.daferarevalo.bibliotecapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}