package com.daferarevalo.bibliotecapp.ui.miperfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentPerfilBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PerfilFragment : Fragment() {

    private lateinit var binding: FragmentPerfilBinding

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