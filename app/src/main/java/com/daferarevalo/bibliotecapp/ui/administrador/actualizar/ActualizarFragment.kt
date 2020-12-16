package com.daferarevalo.bibliotecapp.ui.administrador.actualizar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentActualizarBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ActualizarFragment : Fragment() {

    private lateinit var binding: FragmentActualizarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_actualizar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentActualizarBinding.bind(view)

        binding.actualizarButton.setOnClickListener {
            buscarDesdeFirebase()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buscarDesdeFirebase() {

        val database = FirebaseDatabase.getInstance()
        val myReservasUsuarioRef =
            database.getReference("libros")

        val formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        val fechaActual = LocalDateTime.now().plusDays(20).format(formatters)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val libroServer = dato.getValue(LibroServer::class.java)
                    if (libroServer?.fechaVencimiento == fechaActual) {
                        borrarReservaAuto(libroServer?.id)
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["estado"] = "Disponible"
                        libroServer?.id.let {
                            myReservasUsuarioRef.child(libroServer?.id.toString())
                                .updateChildren(childUpdates)
                        }
                        Toast.makeText(context, "hecho", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myReservasUsuarioRef.addListenerForSingleValueEvent(postListener)
    }

    private fun borrarReservaAuto(idLibro: String?) {
        val database = FirebaseDatabase.getInstance()
        val myUsuariosRef =
            database.getReference("usuarios")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val usuario = data.getValue(Usuario::class.java)
                    usuario?.let {
                        myUsuariosRef.child(usuario.id.toString()).child("misReservas")
                            .child(idLibro.toString()).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuariosRef.addListenerForSingleValueEvent(postListener)
    }

}