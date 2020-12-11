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
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        binding.puntuacionLibroRatingBar.setRating(libroDetalle.promedio)
        binding.puntuacionLibroRatingBar.isEnabled = false

        binding.reservarButton.setOnClickListener {

            actualizarEstadoLibroFirebase(libroDetalle.id.toString())

            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uidUsuario = user.uid
                reservarLibroEnFirebase(uidUsuario, libroDetalle)
            }
            Toast.makeText(context, "Reservado", Toast.LENGTH_SHORT).show()
        }

        binding.anadirResenaButton.setOnClickListener {
            val action =
                DetalleDialogFragmentDirections.actionDetalleDialogFragmentToResenaFragment(
                    libroDetalle
                )
            findNavController().navigate(action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reservarLibroEnFirebase(
        uidUsuario: String,
        libroDetalle: LibroServer
    ) {
        val fechaInicialAux = LocalDateTime.now()
        val fechaFinalAux = fechaInicialAux.plusDays(2)

        val fechaInicial = fechaInicialAux.format(DateTimeFormatter.ISO_DATE)
        val fechaFinal = fechaFinalAux.format(DateTimeFormatter.ISO_DATE)

        val database = FirebaseDatabase.getInstance()
        val myReservaRef = database.getReference("usuarios")

        //val id = myReservaRef.push().key.toString()
        val reservasLibroServer =
            ReservasUsuarioServer(
                libroDetalle.id,
                libroDetalle.titulo,
                libroDetalle.autor,
                fechaInicial,
                fechaFinal,
                libroDetalle.imagen
            )
        uidUsuario.let {
            myReservaRef.child(uidUsuario).child("reservas").child(libroDetalle.id.toString())
                .setValue(reservasLibroServer)
        }
    }

    private fun actualizarEstadoLibroFirebase(id: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("libros")
        val childUpdates = HashMap<String, Any>()
        childUpdates["estado"] = "reservado"
        id.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        //Toast.makeText(context, "DataBase actualizada", Toast.LENGTH_SHORT).show()
    }

    private fun setDetallesLibro(libroDetalle: LibroServer) {
        binding.tituloTextView.text = libroDetalle.titulo
        binding.autorTextView.text = libroDetalle.autor
        binding.categoriaTextView.text = libroDetalle.categoria
        binding.signaturaTextView.text = libroDetalle.signatura
        binding.estadoTextView.text = libroDetalle.estado
        if (libroDetalle.imagen != "")
            Picasso.get().load(libroDetalle.imagen).into(binding.librosImageView)
    }
}