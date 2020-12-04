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
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ResenaFragment : Fragment() {

    private lateinit var binding: FragmentResenaBinding
    private var nombreUsuario: String = ""

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

            val uidUsuario = user.uid

            buscarUsuarioEnFirebase(uidUsuario)




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
                    puntuacionPromedio
                    //comentario
                )

                agregarComentarioLibroFirebase(
                    libroDetalle.id.toString(),
                    nombreUsuario,
                    comentario,
                    puntuacionActual
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

    private fun buscarUsuarioEnFirebase(uidUsuario: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val usuario = data.getValue(Usuario::class.java)
                    if (usuario?.id == uidUsuario) {
                        binding.userTextView.text = usuario.nombre
                        nombreUsuario = usuario.nombre
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuarioRef.addValueEventListener(postListener)
    }

    private fun agregarComentarioLibroFirebase(
        idLibro: String,
        nombreUsuario: String?,
        comentario: String,
        puntuacionActual: Int
    ) {
        val database = FirebaseDatabase.getInstance()
        val myComentarioRef = database.getReference("libros").child(idLibro).child("comentarios")

        nombreUsuario?.let { myComentarioRef.child(it).child("comentario").setValue(comentario) }
        nombreUsuario?.let {
            myComentarioRef.child(it).child("puntuacion").setValue(puntuacionActual)
        }
    }

    private fun actualizarPuntuacionLibroFirebase(
        uidUsuario: String,
        idLibro: String,
        puntuacionLibro: Int,
        cantidadDePuntuaciones: Int,
        puntuacionPromedio: Float,
        //comentario: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("libros")
        val childUpdates = HashMap<String, Any>()
        childUpdates["puntuacion"] = puntuacionLibro
        childUpdates["cantidadDePuntuaciones"] = cantidadDePuntuaciones
        childUpdates["promedio"] = puntuacionPromedio
        //childUpdates["comentario"] = comentario
        idLibro.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        Toast.makeText(context, "Puntuación almacenada", Toast.LENGTH_SHORT).show()
    }
}