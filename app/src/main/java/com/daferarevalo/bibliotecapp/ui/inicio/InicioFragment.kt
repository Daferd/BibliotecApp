package com.daferarevalo.bibliotecapp.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentInicioBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class InicioFragment : Fragment(), LibrosRVAdapter.OnItemClickListener {
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

        librosRVAdapter = LibrosRVAdapter(librosList as ArrayList<LibroServer>, this@InicioFragment)

        binding.librosRecyclerView.adapter = librosRVAdapter

        cargarDesdeFirebase()

        binding.librosSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                librosRVAdapter.filter.filter(newText)
                return false
            }
        })
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

    override fun onItemClick(libro: LibroServer) {
        val action = InicioFragmentDirections.actionNavInicioToNavDetalleLibro(libro)
        findNavController().navigate(action)
        //val dialog = DetalleDialogFragment()
        //dialog.show(supp)
    }

}