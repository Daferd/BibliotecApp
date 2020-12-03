package com.daferarevalo.bibliotecapp.ui.detalleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentResenaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ResenaFragment : Fragment() {

    private lateinit var binding: FragmentResenaBinding

    //private var puntuacionlibro : Float = 0.0F
    //private var cantidadDePuntuaciones : Int = 0
    //private var promedio : Float = 0.0F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resena, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResenaBinding.bind(view)

        var puntuacionActual: Int = 0
        var puntuacionLibro: Int = 0
        var cantidadDePuntuaciones: Int = 0
        var puntuacionPromedio: Float = 0.0F


        val args: ResenaFragmentArgs by navArgs()
        val libroDetalle = args.libroDetalle

        binding.tituloEventoTextView.text = libroDetalle.titulo

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val nombreUsuario = user.email
            val uidUsuario = user.uid
            binding.userTextView.text = nombreUsuario.toString()

            binding.resenaRatingBar.setOnRatingBarChangeListener { ratingBar, puntuacion, b ->
                puntuacionActual = puntuacion.toInt()
                puntuacionLibro = puntuacionActual + libroDetalle.puntuacion
                cantidadDePuntuaciones = libroDetalle.cantidadDePuntuaciones + 1
                puntuacionPromedio = (puntuacionLibro / cantidadDePuntuaciones).toFloat()
            }

            binding.enviarButton.setOnClickListener {

                val comentario = binding.comentarioEditText.text.toString()
                actualizarPuntuacionLibroFirebase(
                    uidUsuario,
                    libroDetalle.id.toString(),
                    puntuacionLibro,
                    cantidadDePuntuaciones,
                    puntuacionPromedio,
                    comentario
                )
                /*actualizarPuntuacionUsuarioFirebase(
                     uidUsuario,
                     libroDetalle.id.toString(),
                     puntuacionActual
                 )*/
                Toast.makeText(context, "Reseña guardada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarPuntuacionLibroFirebase(
        uidUsuario: String,
        idLibro: String,
        puntuacionLibro: Int,
        cantidadDePuntuaciones: Int,
        puntuacionPromedio: Float,
        comentario: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("libros")
        val childUpdates = HashMap<String, Any>()
        childUpdates["puntuacion"] = puntuacionLibro
        childUpdates["cantidadDePuntuaciones"] = cantidadDePuntuaciones
        childUpdates["promedio"] = puntuacionPromedio
        childUpdates["comentario"] = comentario
        idLibro.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        Toast.makeText(context, "Puntuación almacenada", Toast.LENGTH_SHORT).show()
    }
}