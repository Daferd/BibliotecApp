package com.daferarevalo.bibliotecapp.ui.misPrestamos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentMisPrestamosBinding
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.daferarevalo.bibliotecapp.ui.administrador.reservas.ReservasRVAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MisPrestamosFragment : Fragment(), ReservasRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMisPrestamosBinding
    private var prestamosUsuarioList: MutableList<ReservasServer> = mutableListOf()
    private lateinit var librosPrestadosRVAdapter: ReservasRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_prestamos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMisPrestamosBinding.bind(view)

        binding.misPrestamosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.misPrestamosRecyclerView.setHasFixedSize(true)

        librosPrestadosRVAdapter =
            ReservasRVAdapter(
                prestamosUsuarioList as ArrayList<ReservasServer>,
                this@MisPrestamosFragment
            )

        binding.misPrestamosRecyclerView.adapter = librosPrestadosRVAdapter

        cargarDesdeFirebase()

        librosPrestadosRVAdapter.notifyDataSetChanged()

    }

    private fun cargarDesdeFirebase() {
        prestamosUsuarioList.clear()

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid

            val database = FirebaseDatabase.getInstance()
            val myPrestamosUsuarioRef =
                database.getReference("usuarios").child(uidUsuario).child("misPrestamos")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dato: DataSnapshot in snapshot.children) {
                        val prestamosServer = dato.getValue(ReservasServer::class.java)
                        prestamosServer?.let { prestamosUsuarioList.add(it) }
                    }
                    librosPrestadosRVAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myPrestamosUsuarioRef.addListenerForSingleValueEvent(postListener)
        }
    }

    override fun onItemClick(reservaLibro: ReservasServer) {
        Toast.makeText(context, "función no implementada", Toast.LENGTH_SHORT).show()
    }

}
