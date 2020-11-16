package com.daferarevalo.bibliotecapp.ui.registro

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.databinding.ActivityRegistroBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistroBinding

    companion object {
        private val TAG = RegistroActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.registrarRegistroButton.setOnClickListener {
            val nombre = binding.nombreRegistroEditText.text.toString()
            val correo = binding.correoRegistroEditText.text.toString()
            val contrasena = binding.contrasenaRegistroEditText.text.toString()
            val repcontrasena = binding.repcontrasenaRegistroEditText.text.toString()

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show()
            } else if (contrasena != repcontrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                registroEnFirebase(correo, contrasena, nombre)
            }
        }
    }

    private fun registroEnFirebase(correo: String, contrasena: String, nombre: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val uid = auth.currentUser?.uid
                    crearUsuarioEnBaseDatos(uid, nombre, correo)
                    //val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun crearUsuarioEnBaseDatos(uid: String?, nombre: String, correo: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsersReference = database.getReference("usuarios")

        val usuario = Usuario(uid, nombre, correo)
        uid?.let { myUsersReference.child(uid).setValue(usuario) }

        goToLoginActivity()
    }

    private fun goToLoginActivity() {
        onBackPressed()
    }


}