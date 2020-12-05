package com.daferarevalo.bibliotecapp.ui.detalleDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ComentariosItemBinding
import com.daferarevalo.bibliotecapp.server.ComentarioServer

class ComentariosRVAdapter(var comentariosList: ArrayList<ComentarioServer>) :
    RecyclerView.Adapter<ComentariosRVAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comentarios_item, parent, false)
        return ComentarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentariosList[position]
        holder.bindComentario(comentario)
    }

    override fun getItemCount(): Int {
        return comentariosList.size
    }

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ComentariosItemBinding.bind(itemView)

        fun bindComentario(comentario: ComentarioServer) {
            binding.usuarioTextView.text = comentario.usuario
            binding.miPuntuacionRatingBar.setRating(comentario.puntuacion.toFloat())
            binding.comentarioTextView.text = comentario.comentario
        }
    }
}