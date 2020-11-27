package com.daferarevalo.bibliotecapp.ui.misReservas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.LibrosReservadosItemBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class LibrosReservadosRVAdapter(
    var reservasUsuarioList: ArrayList<ReservasUsuarioServer>
) : RecyclerView.Adapter<LibrosReservadosRVAdapter.LibrosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrosViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.libros_reservados_item, parent, false)
        return LibrosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LibrosViewHolder, position: Int) {
        val libroReservado = reservasUsuarioList[position]
        holder.bindLibroReservado(libroReservado)
    }

    override fun getItemCount(): Int {
        return reservasUsuarioList.size
    }

    class LibrosViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = LibrosReservadosItemBinding.bind(itemView)

        fun bindLibroReservado(reservaLibro: ReservasUsuarioServer) {
            binding.tituloTextView.text = reservaLibro.titulo
            binding.autorTextView.text = reservaLibro.autor
            if (reservaLibro.imagen != "")
                Picasso.get().load(reservaLibro.imagen).into(binding.librosImageView)

            binding.eliminarReservaButton.setOnClickListener {
                actualizarDatabaseFirebase(reservaLibro.id.toString())
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    val uidUsuario = user.uid
                    borrarDeFaribase(uidUsuario, reservaLibro.id)
                }
            }
        }

        private fun borrarDeFaribase(uidUsuario: String, idLibro: String?) {

            //nombreBuscarTextInputLayout.error = null

            val database = FirebaseDatabase.getInstance()
            val myDeudoresRef =
                database.getReference("usuarios").child(uidUsuario).child("reservas")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        val reservasUsuarioServer = data.getValue(ReservasUsuarioServer::class.java)
                        reservasUsuarioServer?.let {
                            myDeudoresRef.child(idLibro.toString()).removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myDeudoresRef.addValueEventListener(postListener)
        }

        private fun actualizarDatabaseFirebase(idLibro: String) {
            val database = FirebaseDatabase.getInstance()
            val myLibroRef = database.getReference("libros")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        val libroServer = data.getValue(LibroServer::class.java)
                        if (libroServer?.id.equals(idLibro)) {
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["estado"] = "Disponible"
                            idLibro.let { myLibroRef.child(idLibro).updateChildren(childUpdates) }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myLibroRef.addValueEventListener(postListener)

        }

    }
}