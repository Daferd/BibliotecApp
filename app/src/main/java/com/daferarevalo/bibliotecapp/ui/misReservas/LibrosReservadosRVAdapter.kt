package com.daferarevalo.bibliotecapp.ui.misReservas


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.MisReservasItemBinding
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.squareup.picasso.Picasso
import java.util.*

class LibrosReservadosRVAdapter(
    var reservasUsuarioList: ArrayList<ReservasUsuarioServer>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LibrosReservadosRVAdapter.LibrosReservadosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrosReservadosViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mis_reservas_item, parent, false)
        return LibrosReservadosViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: LibrosReservadosViewHolder, position: Int) {
        val libroReservado = reservasUsuarioList[position]
        holder.bindLibroReservado(libroReservado)
    }

    override fun getItemCount(): Int {
        return reservasUsuarioList.size
    }

    class LibrosReservadosViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = MisReservasItemBinding.bind(itemView)

        fun bindLibroReservado(reservaLibro: ReservasUsuarioServer) {
            binding.tituloTextView.text = reservaLibro.titulo
            binding.autorTextView.text = reservaLibro.autor
            binding.fechaVencimientoTextView.text = reservaLibro.fechaVencimiento
            if (reservaLibro.imagen != "")
                Picasso.get().load(reservaLibro.imagen).into(binding.librosImageView)

            binding.itemCardView.setOnClickListener {
                onItemClickListener.onItemClick(reservaLibro)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(reservaLibro: ReservasUsuarioServer)
    }
}