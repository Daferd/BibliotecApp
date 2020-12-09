package com.daferarevalo.bibliotecapp.ui.miperfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentPerfilBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.daferarevalo.bibliotecapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class PerfilFragment : Fragment() {

    private lateinit var binding: FragmentPerfilBinding

    //private val TAG = PerfilFragment::class.java.simpleName

    //private var correoActual: String? = ""

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

        val navController: NavController = Navigation.findNavController(view)

        binding.infoPersonalButton.setOnClickListener {
            navController.navigate(R.id.nav_informacionPersonal)
        }

        binding.misPrestamosButton.setOnClickListener {
            navController.navigate(R.id.nav_misPrestamos)
        }
        //val navController2 : NavController = Navigation.findNavController(view)
        binding.misReservasButton.setOnClickListener {
            navController.navigate(R.id.nav_misReservas)
        }
        //val navController3 : NavController = Navigation.findNavController(view)
        binding.misEventosButton.setOnClickListener {
            navController.navigate(R.id.nav_misEventos)
        }

        binding.cerrarSesionButton.setOnClickListener {
            val auth = FirebaseAuth.getInstance().signOut()
            goToLoginActivity()
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
                        if (usuarioServer?.foto != "")
                            Picasso.get().load(usuarioServer?.foto).into(binding.perfilImageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuariosRef.addValueEventListener(postListener)
    }

    private fun goToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        //finish()
    }


    companion object
}