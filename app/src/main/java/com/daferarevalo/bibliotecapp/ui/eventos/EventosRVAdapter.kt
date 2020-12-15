package com.daferarevalo.bibliotecapp.ui.eventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.EventosItemBinding
import com.daferarevalo.bibliotecapp.server.EventoServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        private var band = true

        fun bindEvento(evento: EventoServer) {
            binding.tituloEventoTextView.text = evento.titulo
            binding.ubicacionEventoTextView.text = evento.ubicacion
            binding.fechaEventoTextView.text = evento.fecha
            binding.horaEventoTextView.text = evento.hora

            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uidUsuario = user.uid
                buscarEventoUsuario(uidUsuario, evento.id, evento.asistentes, band)
                binding.asistireSwitch.setOnClickListener {
                    if (binding.asistireSwitch.isChecked) {
                        evento.asistentes += 1
                        asistenciaEventoEnFirebase(
                            uidUsuario,
                            evento.id,
                            evento.titulo,
                            evento.ubicacion,
                            evento.fecha,
                            evento.hora,
                            evento.asistentes
                        )
                    } else {
                        band = false
                        evento.asistentes -= 1
                        buscarEventoUsuario(uidUsuario, evento.id, evento.asistentes, band)
                    }
                }
            }
        }


        private fun buscarEventoUsuario(
            uidUsuario: String,
            idEvento: String?,
            asistentes: Int,
            band: Boolean
        ) {
            val database = FirebaseDatabase.getInstance()
            val myEventoUsuarioRef =
                database.getReference("usuarios").child(uidUsuario).child("misEventos")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        val eventoServer = data.getValue(EventoServer::class.java)
                        if (eventoServer?.id == idEvento) {
                            if (band) { //se busca si el evento ya esta checkeado
                                binding.asistireSwitch.isChecked = true
                            } else {
                                cancelarEventoFirebase(idEvento, asistentes)
                                eventoServer?.let {
                                    myEventoUsuarioRef.child(idEvento.toString()).removeValue()
                                }
                                this@EventosViewHolder.band = true
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myEventoUsuarioRef.addListenerForSingleValueEvent(postListener)
        }

        private fun cancelarEventoFirebase(idEvento: String?, asistentes: Int) {
            val database = FirebaseDatabase.getInstance()
            val myEventoRef = database.getReference("eventos")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        val eventoServer = data.getValue(EventoServer::class.java)
                        if (eventoServer?.id == idEvento) {
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["asistentes"] = asistentes
                            idEvento.let {
                                myEventoRef.child(idEvento.toString()).updateChildren(childUpdates)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            myEventoRef.addListenerForSingleValueEvent(postListener)
        }

        private fun asistenciaEventoEnFirebase(
            uidUsuario: String,
            idEvento: String?,
            titulo: String,
            ubicacion: String,
            fecha: String,
            hora: String,
            asistentes: Int
        ) {
            val database = FirebaseDatabase.getInstance()
            val myReservaRef = database.getReference("usuarios")

            val eventosUsuarioServer =
                EventoServer(
                    idEvento,
                    titulo,
                    fecha,
                    ubicacion,
                    hora,
                    asistentes
                )
            uidUsuario.let {
                myReservaRef.child(uidUsuario).child("misEventos").child(idEvento.toString())
                    .setValue(eventosUsuarioServer)
            }

            val myEventoRef = database.getReference("eventos")

            idEvento.let { myEventoRef.child(idEvento.toString()).setValue(eventosUsuarioServer) }

        }

    }

}