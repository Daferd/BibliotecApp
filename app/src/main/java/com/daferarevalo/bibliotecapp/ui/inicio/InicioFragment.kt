package com.daferarevalo.bibliotecapp.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentInicioBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class InicioFragment : Fragment() {
    private lateinit var binding: FragmentInicioBinding

    private var librosList: MutableList<LibroServer> = mutableListOf()
    private lateinit var librosRVAdapter: LibrosRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInicioBinding.bind(view)

        binding.librosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.librosRecyclerView.setHasFixedSize(true)

        librosRVAdapter = LibrosRVAdapter(librosList as ArrayList<LibroServer>)

        binding.librosRecyclerView.adapter = librosRVAdapter

        cargarDesdeFirebase()

        librosRVAdapter.notifyDataSetChanged()

        val navController: NavController = Navigation.findNavController(view)
        binding.button.setOnClickListener {
            navController.navigate(R.id.nav_libro)
        }
    }

    private fun cargarDesdeFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myLibrosRef = database.getReference("libros")

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