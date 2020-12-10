package com.daferarevalo.bibliotecapp.ui.misEventos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentMisEventosBinding
import com.daferarevalo.bibliotecapp.server.EventoServer
import com.daferarevalo.bibliotecapp.ui.eventos.EventosRVAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MisEventosFragment : Fragment() {

    private lateinit var binding: FragmentMisEventosBinding

    private var eventosUsuarioList: MutableList<EventoServer> = mutableListOf()
    private lateinit var eventosRVAdapter: EventosRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_eventos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMisEventosBinding.bind(view)

        binding.misEventosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        binding.misEventosRecyclerView.setHasFixedSize(true)

        eventosRVAdapter = EventosRVAdapter(eventosUsuarioList as ArrayList<EventoServer>)

        binding.misEventosRecyclerView.adapter = eventosRVAdapter

        cargarDesdeFirebase()

        eventosRVAdapter.notifyDataSetChanged()

    }

    private fun cargarDesdeFirebase() {
        eventosUsuarioList.clear()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uidUsuario = user.uid

            val database = FirebaseDatabase.getInstance()
            val myEventosUsuarioRef =
                database.getReference("usuarios").child(uidUsuario).child("misEventos")

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dato: DataSnapshot in snapshot.children) {
                        val misEventosServer = dato.getValue(EventoServer::class.java)
                        misEventosServer?.let { eventosUsuarioList.add(it) }
                    }
                    eventosRVAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            myEventosUsuarioRef.addListenerForSingleValueEvent(postListener)
        }
    }

}