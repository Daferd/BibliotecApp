package com.daferarevalo.bibliotecapp.ui.administrador.prestamos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentPrestamosBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.daferarevalo.bibliotecapp.ui.administrador.reservas.ReservasRVAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PrestamosFragment : Fragment(), ReservasRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentPrestamosBinding
    private var prestamosList: MutableList<ReservasServer> = mutableListOf()
    private lateinit var prestamosRVAdapter: ReservasRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prestamos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPrestamosBinding.bind(view)

        binding.prestamosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.prestamosRecyclerView.setHasFixedSize(true)

        prestamosRVAdapter =
            ReservasRVAdapter(
                prestamosList as ArrayList<ReservasServer>,
                this@PrestamosFragment
            )

        binding.prestamosRecyclerView.adapter = prestamosRVAdapter

        librosPrestadosFirebase()

        prestamosRVAdapter.notifyDataSetChanged()

    }

    private fun librosPrestadosFirebase() {
        prestamosList.clear()

        val database = FirebaseDatabase.getInstance()
        val myLibrosRef = database.getReference("libros")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val libro = data.getValue(LibroServer::class.java)
                    if (libro?.estado == "prestado") {
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
        val myUsuarioRef = database.getReference("usuarios").child(reservadoPor).child("prestamos")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val prestamoServer = data.getValue(ReservasServer::class.java)
                    if (prestamoServer?.id == idLibro)
                        prestamoServer?.let { prestamosList.add(it) }
                }
                prestamosRVAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuarioRef.addListenerForSingleValueEvent(postListener)
    }

    override fun onItemClick(reservaLibro: ReservasServer) {
        TODO("Not yet implemented")
    }

}