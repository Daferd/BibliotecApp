package com.daferarevalo.bibliotecapp.ui.eventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.EventosItemBinding
import com.daferarevalo.bibliotecapp.server.EventoServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
            binding.tituloEventoTextView.text = evento.titulo
            binding.ubicacionEventoTextView.text = evento.ubicacion
            binding.fechaEventoTextView.text = evento.fecha
            binding.horaEventoTextView.text = evento.hora
            binding.asistireSwitch.setOnClickListener {
                if (binding.asistireSwitch.isChecked) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        val uidUsuario = user.uid
                        asistenciaEventoEnFirebase(
                            uidUsuario,
                            evento.id,
                            evento.titulo,
                            evento.ubicacion,
                            evento.fecha,
                            evento.hora
                        )
                    }

                }
            }
        }

        private fun asistenciaEventoEnFirebase(
            uidUsuario: String,
            idEvento: String?,
            titulo: String,
            ubicacion: String,
            fecha: String,
            hora: String
        ) {
            val database = FirebaseDatabase.getInstance()
            val myReservaRef = database.getReference("usuarios")

            //val id = myReservaRef.push().key.toString()
            val eventosUsuarioServer =
                EventoServer(
                    idEvento,
                    titulo,
                    fecha,
                    ubicacion,
                    hora,
                    hora
                )
            uidUsuario.let {
                myReservaRef.child(uidUsuario).child("misEventos").child(idEvento.toString())
                    .setValue(eventosUsuarioServer)
            }

        }

    }

}