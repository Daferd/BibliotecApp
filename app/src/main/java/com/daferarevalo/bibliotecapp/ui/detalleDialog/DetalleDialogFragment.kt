package com.daferarevalo.bibliotecapp.ui.detalleDialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentDetalleDialogBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetalleDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDetalleDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_detalle_dialog, container, false)
        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetalleDialogBinding.bind(view)

        val args: DetalleDialogFragmentArgs by navArgs()
        val libroDetalle = args.libroSeleccionado
        setDetallesLibro(libroDetalle)


        binding.reservarButton.setOnClickListener {
            verificarDisponibilidadReserva(libroDetalle)
        }

        binding.anadirResenaButton.setOnClickListener {
            val action =
                DetalleDialogFragmentDirections.actionDetalleDialogFragmentToResenaFragment(
                    libroDetalle
                )
            findNavController().navigate(action)
        }
    }

    private fun verificarDisponibilidadReserva(libroDetalle: LibroServer) {
        val database = FirebaseDatabase.getInstance()
        val myReservaRef = database.getReference("libros")

        val postListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O) // Se necesita para usar la clase LocalDateTime
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val libroServer = data.getValue(LibroServer::class.java)
                    if (libroServer?.id == libroDetalle.id) { // SE BUSCA EL LIBRO QUE SE QUIERE RESERVAR
                        if (libroServer?.estado == "reservado") {  //SE VERIFICA QUE EL LIBRO NO ESTE RESERVADO
                            Toast.makeText(context, "El libro esta reservado", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu")

                            val fechaInicial = LocalDateTime.now()
                            val fechaFinalAux = fechaInicial.plusDays(2)

                            val fechaVencimiento = fechaFinalAux.format(formatters)

                            actualizarEstadoLibroFirebase(
                                libroDetalle.id.toString(),
                                fechaVencimiento
                            )
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                val uidUsuario = user.uid
                                val nombre = buscarUsuarioFirebase(uidUsuario)
                                reservarLibroEnFirebase(
                                    uidUsuario,
                                    libroDetalle,
                                    fechaVencimiento,
                                    nombre
                                )
                            }
                            Toast.makeText(context, "Reservado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myReservaRef.addListenerForSingleValueEvent(postListener)
    }

    private fun buscarUsuarioFirebase(uidUsuario: String): String {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")
        var nombre = ""

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val usuario = data.getValue(Usuario::class.java)
                    if (usuario?.id == uidUsuario) {
                        nombre = usuario.nombre
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuarioRef.addListenerForSingleValueEvent(postListener)
        return nombre
    }


    @RequiresApi(Build.VERSION_CODES.O) // Sse necesita para usar la clase LocalDateTime
    private fun reservarLibroEnFirebase(
        uidUsuario: String,
        libroDetalle: LibroServer,
        fechaVencimiento: String,
        nombre: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val myReservaRef = database.getReference("usuarios")

        //val id = myReservaRef.push().key.toString()
        val reservasServer =
            ReservasServer(
                libroDetalle.id,
                libroDetalle.titulo,
                libroDetalle.autor,
                fechaVencimiento,
                libroDetalle.imagen,
                nombre,
                uidUsuario
            )
        uidUsuario.let {
            myReservaRef.child(uidUsuario).child("reservas").child(libroDetalle.id.toString())
                .setValue(reservasServer)
        }
    }

    private fun actualizarEstadoLibroFirebase(id: String, fechaVencimiento: String) {
        val database = FirebaseDatabase.getInstance()
        val myLibroRef = database.getReference("libros")

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid
            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["estado"] = "reservado"
                    childUpdates["fechaVencimiento"] = fechaVencimiento
                    childUpdates["reservadoPor"] = uidUsuario
                    id.let { myLibroRef.child(it).updateChildren(childUpdates) }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myLibroRef.addListenerForSingleValueEvent(postListener)
        }


    }

    private fun setDetallesLibro(libroDetalle: LibroServer) {
        binding.tituloTextView.text = libroDetalle.titulo
        binding.autorTextView.text = libroDetalle.autor
        binding.categoriaTextView.text = libroDetalle.categoria
        binding.signaturaTextView.text = libroDetalle.signatura
        binding.estadoTextView.text = libroDetalle.estado
        binding.puntuacionLibroRatingBar.setRating(libroDetalle.promedio)
        binding.puntuacionLibroRatingBar.isEnabled = false
        if (libroDetalle.imagen != "")
            Picasso.get().load(libroDetalle.imagen).into(binding.librosImageView)
    }
}