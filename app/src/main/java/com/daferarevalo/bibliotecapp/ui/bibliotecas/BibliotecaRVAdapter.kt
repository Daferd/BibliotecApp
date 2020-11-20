package com.daferarevalo.bibliotecapp.ui.bibliotecas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.BibliotecasItemBinding
import com.daferarevalo.bibliotecapp.server.BibliotecaServer
import com.squareup.picasso.Picasso

class BibliotecaRVAdapter(var bibliotecasList: ArrayList<BibliotecaServer>) :
    RecyclerView.Adapter<BibliotecaRVAdapter.BibliotecaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bibliotecas_item, parent, false)
        return BibliotecaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
        val biblioteca = bibliotecasList[position]
        holder.bindBiblioteca(biblioteca)
    }

    override fun getItemCount(): Int {
        return bibliotecasList.size
    }

    class BibliotecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = BibliotecasItemBinding.bind(itemView)

        fun bindBiblioteca(biblioteca: BibliotecaServer) {
            binding.tituloBibliotecaTextView.text = biblioteca.titulo
            binding.direccionBibliotecaTextView.text = biblioteca.direccion
            Picasso.get().load(biblioteca.imagen).into(binding.bibliotecaImageView)
        }
    }
}