package com.daferarevalo.bibliotecapp.ui.miperfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentPerfilBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.daferarevalo.bibliotecapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PerfilFragment : Fragment() {

    private lateinit var binding: FragmentPerfilBinding

    //private val TAG = PerfilFragment::class.java.simpleName

    //private lateinit var correoActual: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPerfilBinding.bind(view)

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid
            buscarEnFirebase(uidUsuario)
        }

        binding.actualizarPerfilButton.setOnClickListener {

            val nuevoNombre = binding.nombrePerfilEditText.text.toString()
            val nuevoCorreo = binding.correoPerfilEditText.text.toString()
            val nuevaContrasena = binding.contrasenaPerfilEditText.text.toString()

            user?.let {
                val uidUsuario = user.uid
                actualizarCorreoFirebase(user, nuevoCorreo)
                actualizarContrasenaFirebase(nuevaContrasena, user)
                actualizarDatabaseFirebase(nuevoNombre, nuevoCorreo, uidUsuario)
                //actualizarDatabaseFirebase(nuevoNombre,nuevoCorreo)
            }
        }

        binding.cerrarSesionButton.setOnClickListener {
            val auth = FirebaseAuth.getInstance().signOut()
            goToLoginActivity()
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        //finish()
    }

    private fun actualizarDatabaseFirebase(
        nuevoNombre: String,
        nuevoCorreo: String,
        uidUsuario: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")
        val childUpdates = HashMap<String, Any>()
        childUpdates["nombre"] = nuevoNombre
        childUpdates["correo"] = nuevoCorreo
        uidUsuario.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        Toast.makeText(context, "DataBase actualizada", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarContrasenaFirebase(
        nuevaContrasena: String,
        user: FirebaseUser?
    ) {
        if (nuevaContrasena.isNotBlank() || nuevaContrasena.isNotEmpty()) {

            user!!.updatePassword(nuevaContrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Contraseña Actualizada", Toast.LENGTH_SHORT).show()
                        //Log.d(TAG, "User password updated.")
                    }
                }
        }
    }


    private fun actualizarCorreoFirebase(
        user: FirebaseUser?,
        nuevoCorreo: String
    ) {
        user!!.updateEmail(nuevoCorreo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Actualizado", Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, "User email address updated.")
                }
            }
    }


    private fun buscarEnFirebase(uidUsuario: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsuariosRef = database.getReference("usuarios")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val usuarioServer = data.getValue(Usuario::class.java)
                    if (usuarioServer?.id.equals(uidUsuario)) {
                        binding.nombrePerfilEditText.setText(usuarioServer?.nombre)
                        binding.correoPerfilEditText.setText(usuarioServer?.correo)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        myUsuariosRef.addValueEventListener(postListener)
    }

    companion object
}