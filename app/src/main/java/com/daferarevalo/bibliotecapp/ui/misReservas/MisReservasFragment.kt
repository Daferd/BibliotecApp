package com.daferarevalo.bibliotecapp.ui.misReservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentMisReservasBinding
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MisReservasFragment : Fragment() {

    private lateinit var binding: FragmentMisReservasBinding

    private var reservasUsuarioList: MutableList<ReservasUsuarioServer> = mutableListOf()
    private lateinit var librosReservadosRVAdapter: LibrosReservadosRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMisReservasBinding.bind(view)

        binding.librosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.librosRecyclerView.setHasFixedSize(true)

        librosReservadosRVAdapter =
            LibrosReservadosRVAdapter(reservasUsuarioList as ArrayList<ReservasUsuarioServer>)

        binding.librosRecyclerView.adapter = librosReservadosRVAdapter

        cargarDesdeFirebase()

        librosReservadosRVAdapter.notifyDataSetChanged()

    }

    private fun cargarDesdeFirebase() {

        reservasUsuarioList.clear()

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid

            val database = FirebaseDatabase.getInstance()
            val myReservasUsuarioRef =
                database.getReference("usuarios").child(uidUsuario).child("reservas")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dato: DataSnapshot in snapshot.children) {
                        val reservasUsuarioServer = dato.getValue(ReservasUsuarioServer::class.java)
                        reservasUsuarioServer?.let { reservasUsuarioList.add(it) }
                    }
                    librosReservadosRVAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            }
            myReservasUsuarioRef.addValueEventListener(postListener)
        }
    }

}