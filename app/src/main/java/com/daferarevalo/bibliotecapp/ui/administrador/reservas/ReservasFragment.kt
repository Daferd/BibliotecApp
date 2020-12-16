package com.daferarevalo.bibliotecapp.ui.administrador.reservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentReservasBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ReservasFragment() : Fragment(), ReservasRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentReservasBinding
    private var reservasList: MutableList<ReservasServer> = mutableListOf()
    private lateinit var reservasRVAdapter: ReservasRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReservasBinding.bind(view)

        binding.reservasRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.reservasRecyclerView.setHasFixedSize(true)

        reservasRVAdapter =
            ReservasRVAdapter(
                reservasList as ArrayList<ReservasServer>,
                this@ReservasFragment
            )

        binding.reservasRecyclerView.adapter = reservasRVAdapter

        librosReservadosFirebase()

        reservasRVAdapter.notifyDataSetChanged()

    }

    private fun librosReservadosFirebase() {
        reservasList.clear()

        val database = FirebaseDatabase.getInstance()
        val myLibrosRef = database.getReference("libros")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val libro = data.getValue(LibroServer::class.java)
                    if (libro?.estado == "reservado") {
                        cargarDesdeFirebase(libro.reservadoPor, libro.id)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myLibrosRef.addListenerForSingleValueEvent(postListener)
    }

    private fun cargarDesdeFirebase(reservadoPor: String, idLibro: String?) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef =
            database.getReference("usuarios").child(reservadoPor).child("misReservas")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val reservasServer = data.getValue(ReservasServer::class.java)
                    if (reservasServer?.id == idLibro)
                        reservasServer?.let { reservasList.add(it) }
                }
                reservasRVAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuarioRef.addListenerForSingleValueEvent(postListener)
    }


    override fun onItemClick(reservaLibro: ReservasServer) {
        val action =
            ReservasFragmentDirections.actionNavReservasToDetalleReservasDialogFragment(reservaLibro)
        findNavController().navigate(action)
    }

}