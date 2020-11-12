package com.daferarevalo.bibliotecapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ActivityLoginBinding
import com.daferarevalo.bibliotecapp.ui.drawer.DrawerActivity
import com.daferarevalo.bibliotecapp.ui.registro.RegistroActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.iniciarSesionButton.setOnClickListener {
            val correo = binding.correoLoginEditText.text.toString()
            val contrasena = binding.contrasenaLoginEditText.text.toString()

            if (correo.isEmpty() || correo.isBlank()) {
                binding.correoLoginEditTextLayout.error = getString(R.string.ingrese_correo)
            } else if (contrasena.isEmpty() || contrasena.isBlank()) {
                binding.correoLoginEditTextLayout.error = null
                binding.contrasenaLoginEditTextLayout.error = getString(R.string.ingrese_contrasena)
            } else {
                goToDrawerActivity()
            }
        }

        binding.registrarLoginButton.setOnClickListener {
            goToRegistroActivity()
        }
    }

    private fun goToRegistroActivity() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    private fun goToDrawerActivity() {
        val intent = Intent(this, DrawerActivity::class.java)
        startActivity(intent)
        finish()
    }
}