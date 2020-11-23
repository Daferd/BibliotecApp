package com.daferarevalo.bibliotecapp.ui.inicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.LibrosItemBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class LibrosRVAdapter(var librosList: ArrayList<LibroServer>) :
    RecyclerView.Adapter<LibrosRVAdapter.LibrosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrosViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.libros_item, parent, false)
        return LibrosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LibrosViewHolder, position: Int) {
        val libro = librosList[position]
        holder.bindLibro(libro)
    }

    override fun getItemCount(): Int {
        return librosList.size
    }

    class LibrosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = LibrosItemBinding.bind(itemView)

        fun bindLibro(libro: LibroServer) {
            binding.tituloTextView.text = libro.titulo
            binding.autorTextView.text = libro.autor
            Picasso.get().load(libro.imagen).into(binding.librosImageView)

            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uidUsuario = user.uid
                binding.reservarButton.setOnClickListener {
                    reservarLibroEnFirebase(uidUsuario, libro.titulo, libro.autor, libro.imagen)
                }
            }
        }

        private fun reservarLibroEnFirebase(
            uidUsuario: String,
            titulo: String,
            autor: String,
            imagen: String
        ) {
            val database = FirebaseDatabase.getInstance()
            val myReservaRef = database.getReference("usuarios")

            val id = myReservaRef.push().key.toString()
            val reservasUsuarioServer = ReservasUsuarioServer(titulo, autor, imagen)

            uidUsuario.let {
                myReservaRef.child(uidUsuario).child("reservas").child(id)
                    .setValue(reservasUsuarioServer)
            }

        }
    }


}
