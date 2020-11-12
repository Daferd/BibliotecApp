package com.daferarevalo.bibliotecapp.ui.registro

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.registrarRegistroButton.setOnClickListener {
            val nombre = binding.nombreRegistroEditText.text.toString()
            val correo = binding.correoRegistroEditText.text.toString()
            val contrasena = binding.contrasenaRegistroEditText.text.toString()

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show()
            } else {
                goToLoginActivity()
            }
        }
    }

    private fun goToLoginActivity() {
        onBackPressed()
    }


}