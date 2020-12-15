package com.daferarevalo.bibliotecapp.ui.bibliotecas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentBibliotecasBinding
import com.daferarevalo.bibliotecapp.server.BibliotecaServer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BibliotecasFragment : Fragment(), BibliotecaRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentBibliotecasBinding

    private var bibliotecasList: MutableList<BibliotecaServer> = mutableListOf()
    private lateinit var bibliotecaRVAdapter: BibliotecaRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bibliotecas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBibliotecasBinding.bind(view)

        binding.bibliotecasRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.bibliotecasRecyclerView.setHasFixedSize(true)

        bibliotecaRVAdapter = BibliotecaRVAdapter(
            bibliotecasList as ArrayList<BibliotecaServer>,
            this@BibliotecasFragment
        )

        binding.bibliotecasRecyclerView.adapter = bibliotecaRVAdapter

        cargarDesdeFirebase()

        bibliotecaRVAdapter.notifyDataSetChanged()

        /*val navController: NavController = Navigation.findNavController(view)
        binding.irMapaButton.setOnClickListener {
            navController.navigate(R.id.nav_biblioMapa)
        }*/
    }

    private fun cargarDesdeFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myBibliotecasRef = database.getReference("bibliotecas")

        bibliotecasList.clear()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val bibliotecaServer = dato.getValue(BibliotecaServer::class.java)
                    bibliotecaServer?.let { bibliotecasList.add(it) }
                }
                bibliotecaRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myBibliotecasRef.addValueEventListener(postListener)
    }

    override fun onItemClick(biblioteca: BibliotecaServer) {
        val action =
            BibliotecasFragmentDirections.actionBibliotecasFragmentToNavBiblioMapa(biblioteca)
        findNavController().navigate(action)
    }
}