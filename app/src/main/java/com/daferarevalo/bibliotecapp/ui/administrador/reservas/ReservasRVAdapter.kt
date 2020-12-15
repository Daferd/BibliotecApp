package com.daferarevalo.bibliotecapp.ui.administrador.reservas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ReservasItemBinding
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.squareup.picasso.Picasso

class ReservasRVAdapter(
    val reservasList: ArrayList<ReservasServer>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ReservasRVAdapter.ReservasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservasViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.reservas_item, parent, false)
        return ReservasRVAdapter.ReservasViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ReservasViewHolder, position: Int) {
        val libroRes = reservasList[position]
        holder.bindLibroRes(libroRes)
    }

    override fun getItemCount(): Int {
        return reservasList.size
    }

    class ReservasViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ReservasItemBinding.bind(itemView)

        fun bindLibroRes(libroRes: ReservasServer) {
            binding.tituloTextView.text = libroRes.titulo
            binding.autorTextView.text = libroRes.autor
            binding.fechaVencimientoTextView.text = libroRes.fechaVencimiento
            binding.usuarioResTextView.text = libroRes.nombreUsuario
            if (libroRes.imagen != "")
                Picasso.get().load(libroRes.imagen).into(binding.librosImageView)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(reservaLibro: ReservasUsuarioServer)
    }
}