package com.daferarevalo.bibliotecapp.ui.Detallelibro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentDetalleLibroBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class DetalleLibroFragment : Fragment() {

    private lateinit var binding: FragmentDetalleLibroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_libro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDetalleLibroBinding.bind(view)

        /*val medidasVentana = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(medidasVentana)

        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels

        val nuevoAncho = ancho * 0.85
        val nuevoAlto = alto * 0.6

        activity?.window?.setLayout(nuevoAncho.toInt(), nuevoAlto.toInt())*/

        val args: DetalleLibroFragmentArgs by navArgs()
        val libroDetalle = args.libroSeleccionado
        setDetallesLibro(libroDetalle)


        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid
            binding.reservarButton.setOnClickListener {

                actualizarDatabaseFirebase(libroDetalle.id.toString())
                reservarLibroEnFirebase(uidUsuario, libroDetalle)
                //Toast.makeText(context, "Reservado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarDatabaseFirebase(id: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("libros")
        val childUpdates = HashMap<String, Any>()
        childUpdates["estado"] = "reservado"
        id.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        Toast.makeText(context, "DataBase actualizada", Toast.LENGTH_SHORT).show()
    }

    private fun setDetallesLibro(libroDetalle: LibroServer) {
        binding.tituloTextView.text = libroDetalle.titulo
        binding.autorTextView.text = libroDetalle.autor
        binding.categoriaTextView.text = libroDetalle.categoria
        binding.signaturaTextView.text = libroDetalle.signatura
        binding.estadoTextView.text = libroDetalle.estado
        Picasso.get().load(libroDetalle.imagen).into(binding.librosImageView)
    }

    private fun reservarLibroEnFirebase(
        uidUsuario: String,
        libroDetalle: LibroServer
    ) {
        val database = FirebaseDatabase.getInstance()
        val myReservaRef = database.getReference("usuarios")

        val id = myReservaRef.push().key.toString()
        val reservasLibroServer =
            ReservasUsuarioServer(id, libroDetalle.titulo, libroDetalle.autor, libroDetalle.imagen)
        uidUsuario.let {
            myReservaRef.child(uidUsuario).child("reservas").child(id)
                .setValue(reservasLibroServer)
        }

    }

}