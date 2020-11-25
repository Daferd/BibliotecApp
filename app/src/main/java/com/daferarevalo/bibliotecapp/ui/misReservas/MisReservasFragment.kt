package com.daferarevalo.bibliotecapp.ui.misReservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentMisReservasBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.ui.inicio.LibrosRVAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MisReservasFragment : Fragment(), LibrosRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMisReservasBinding

    private var librosList: MutableList<LibroServer> = mutableListOf()
    private lateinit var librosRVAdapter: LibrosRVAdapter

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

        librosRVAdapter =
            LibrosRVAdapter(librosList as ArrayList<LibroServer>, this@MisReservasFragment)

        binding.librosRecyclerView.adapter = librosRVAdapter

        cargarDesdeFirebase()

        librosRVAdapter.notifyDataSetChanged()


    }

    private fun cargarDesdeFirebase() {

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid

            val database = FirebaseDatabase.getInstance()
            val myLibrosRef = database.getReference("usuarios").child(uidUsuario).child("reservas")

            librosList.clear()

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dato: DataSnapshot in snapshot.children) {
                        val libroServer = dato.getValue(LibroServer::class.java)
                        libroServer?.let { librosList.add(it) }
                    }
                    librosRVAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myLibrosRef.addValueEventListener(postListener)
        }


    }

    override fun onItemClick(libro: LibroServer) {
        val action = MisReservasFragmentDirections.actionNavMisReservasToNavDetalleLibro(libro)
        findNavController().navigate(action)
    }

}