package com.daferarevalo.bibliotecapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ActivityLoginBinding
import com.daferarevalo.bibliotecapp.ui.drawer.DrawerActivity
import com.daferarevalo.bibliotecapp.ui.registro.RegistroActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        private val TAG = RegistroActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.iniciarSesionButton.setOnClickListener {
            val correo = binding.correoLoginEditText.text.toString()
            val contrasena = binding.contrasenaLoginEditText.text.toString()

            if (correo.isEmpty() || correo.isBlank()) {
                binding.correoLoginEditTextLayout.error = getString(R.string.ingrese_correo)
            } else if (contrasena.isEmpty() || contrasena.isBlank()) {
                binding.correoLoginEditTextLayout.error = null
                binding.contrasenaLoginEditTextLayout.error = getString(R.string.ingrese_contrasena)
            } else {
                loginConFirebase(correo, contrasena)
            }
        }

        binding.registrarLoginButton.setOnClickListener {
            goToRegistroActivity()
        }
    }

    private fun loginConFirebase(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    goToDrawerActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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