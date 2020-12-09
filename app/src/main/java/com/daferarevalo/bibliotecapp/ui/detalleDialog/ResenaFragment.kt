package com.daferarevalo.bibliotecapp.ui.detalleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentResenaBinding
import com.daferarevalo.bibliotecapp.server.ComentarioServer
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ResenaFragment : Fragment() {

    private lateinit var binding: FragmentResenaBinding
    private var nombreUsuario: String = ""

    private var comentariosList: MutableList<ComentarioServer> = mutableListOf()
    private lateinit var comentariosRVAdapter: ComentariosRVAdapter

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
        binding.editarButton.visibility = View.INVISIBLE
        binding.borrarButton.visibility = View.INVISIBLE


        val user = FirebaseAuth.getInstance().currentUser
        user?.let {

            val uidUsuario = user.uid

            buscarUsuarioEnFirebase(uidUsuario)
            buscarComentarioUsuarioEnFirebase(uidUsuario, libroDetalle.id)

            binding.resenaRatingBar.setOnRatingBarChangeListener { ratingBar, puntuacion, b ->
                puntuacionActual = puntuacion.toInt()
                puntuacionLibro = puntuacionActual + libroDetalle.puntuacion
                cantidadDePuntuaciones = libroDetalle.cantidadDePuntuaciones + 1
                puntuacionPromedio = (puntuacionLibro / cantidadDePuntuaciones).toFloat()
            }

            binding.enviarButton.setOnClickListener {

                val comentario = binding.comentarioEditText.text.toString()

                if (puntuacionActual == 0)
                    Toast.makeText(context, "Asigne una puntuación al libro", Toast.LENGTH_SHORT)
                        .show()
                else if (comentario.isEmpty())
                    Toast.makeText(context, "Comente el libro", Toast.LENGTH_SHORT).show()
                else {
                    actualizarPuntuacionLibroFirebase(
                        libroDetalle.id.toString(),
                        puntuacionLibro,
                        cantidadDePuntuaciones,
                        puntuacionPromedio
                    )

                    agregarComentarioLibroFirebase(
                        uidUsuario,
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

            binding.borrarButton.setOnClickListener {

            }
        }

        binding.comentariosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.comentariosRecyclerView.setHasFixedSize(true)

        comentariosRVAdapter = ComentariosRVAdapter(comentariosList as ArrayList<ComentarioServer>)

        binding.comentariosRecyclerView.adapter = comentariosRVAdapter

        cargarDesdeFirebase(libroDetalle.id)

        comentariosRVAdapter.notifyDataSetChanged()
    }

    private fun buscarComentarioUsuarioEnFirebase(uidUsuario: String, idLibro: String?) {
        val database = FirebaseDatabase.getInstance()
        val myLibroRef =
            database.getReference("libros").child(idLibro.toString()).child("comentarios")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val comentario = data.getValue(ComentarioServer::class.java)
                    if (comentario?.id == uidUsuario) {
                        binding.resenaRatingBar.isEnabled = false
                        binding.comentarioEditText.isEnabled = false
                        binding.comentarioEditText.setText(comentario.comentario)
                        binding.resenaRatingBar.setRating(comentario.puntuacion.toFloat())
                        binding.editarButton.visibility = View.VISIBLE
                        binding.borrarButton.visibility = View.VISIBLE
                        binding.enviarButton.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myLibroRef.addValueEventListener(postListener)
    }


    private fun cargarDesdeFirebase(idLibro: String?) {
        val database = FirebaseDatabase.getInstance()
        val myComentarioRef =
            database.getReference("libros").child(idLibro.toString()).child("comentarios")

        comentariosList.clear()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val comentarioServer = dato.getValue(ComentarioServer::class.java)
                    comentarioServer?.let { comentariosList.add(it) }
                }
                comentariosRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myComentarioRef.addValueEventListener(postListener)
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
        uidUsuario: String,
        idLibro: String,
        nombreUsuario: String?,
        comentario: String,
        puntuacionActual: Int
    ) {
        val database = FirebaseDatabase.getInstance()
        val myComentarioRef = database.getReference("libros").child(idLibro).child("comentarios")

        val comentarioServer =
            ComentarioServer(uidUsuario, nombreUsuario.toString(), comentario, puntuacionActual)

        uidUsuario?.let { myComentarioRef.child(it).setValue(comentarioServer) }
    }

    private fun actualizarPuntuacionLibroFirebase(
        idLibro: String,
        puntuacionLibro: Int,
        cantidadDePuntuaciones: Int,
        puntuacionPromedio: Float,
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