package com.daferarevalo.bibliotecapp.ui.inicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.LibrosItemBinding
import com.daferarevalo.bibliotecapp.server.LibroServer

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
        }
    }
}
