package com.daferarevalo.bibliotecapp.ui.eventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.EventosItemBinding
import com.daferarevalo.bibliotecapp.server.EventoServer

class EventosRVAdapter(var eventosList: ArrayList<EventoServer>) :
    RecyclerView.Adapter<EventosRVAdapter.EventosViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventosViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.eventos_item, parent, false)
        return EventosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        val evento = eventosList[position]
        holder.bindEvento(evento)
    }

    override fun getItemCount(): Int {
        return eventosList.size
    }

    class EventosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = EventosItemBinding.bind(itemView)

        fun bindEvento(evento: EventoServer) {
            binding.tituloEventoEditText.text = evento.titulo
        }
    }

}