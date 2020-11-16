package com.daferarevalo.bibliotecapp.ui.eventos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentEventosBinding
import com.daferarevalo.bibliotecapp.server.EventoServer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventosFragment : Fragment() {

    private lateinit var binding: FragmentEventosBinding

    private var eventosList: MutableList<EventoServer> = mutableListOf()
    private lateinit var eventosRVAdapter: EventosRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eventos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEventosBinding.bind(view)

        binding.eventosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.eventosRecyclerView.setHasFixedSize(true)

        eventosRVAdapter = EventosRVAdapter(eventosList as ArrayList<EventoServer>)

        binding.eventosRecyclerView.adapter = eventosRVAdapter

        cargarDesdeFirebase()

        eventosRVAdapter.notifyDataSetChanged()
    }

    private fun cargarDesdeFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myEventosRef = database.getReference("eventos")

        eventosList.clear()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val eventoServer = dato.getValue(EventoServer::class.java)
                    eventoServer?.let { eventosList.add(it) }
                }
                eventosRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myEventosRef.addValueEventListener(postListener)
    }

    companion object
}