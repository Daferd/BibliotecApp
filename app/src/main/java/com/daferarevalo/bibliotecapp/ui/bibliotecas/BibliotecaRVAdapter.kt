package com.daferarevalo.bibliotecapp.ui.bibliotecas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.BibliotecasItemBinding
import com.daferarevalo.bibliotecapp.server.BibliotecaServer
import com.squareup.picasso.Picasso

class BibliotecaRVAdapter(
    var bibliotecasList: ArrayList<BibliotecaServer>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BibliotecaRVAdapter.BibliotecaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bibliotecas_item, parent, false)
        return BibliotecaViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
        val biblioteca = bibliotecasList[position]
        holder.bindBiblioteca(biblioteca)
    }

    override fun getItemCount(): Int {
        return bibliotecasList.size
    }

    class BibliotecaViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = BibliotecasItemBinding.bind(itemView)

        fun bindBiblioteca(biblioteca: BibliotecaServer) {
            binding.tituloBibliotecaTextView.text = biblioteca.titulo
            binding.direccionBibliotecaTextView.text = biblioteca.direccion
            Picasso.get().load(biblioteca.imagen).into(binding.bibliotecaImageView)

            binding.itemBibliotecaCardView.setOnClickListener {
                onItemClickListener.onItemClick(biblioteca)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(biblioteca: BibliotecaServer)
    }
}