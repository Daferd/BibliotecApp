package com.daferarevalo.bibliotecapp.ui.misReservas

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentMisReservasBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MisReservasFragment : Fragment(), LibrosReservadosRVAdapter.OnItemClickListener {

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMisReservasBinding.bind(view)

        binding.librosRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.librosRecyclerView.setHasFixedSize(true)

        librosReservadosRVAdapter =
            LibrosReservadosRVAdapter(
                reservasUsuarioList as ArrayList<ReservasUsuarioServer>,
                this@MisReservasFragment
            )

        binding.librosRecyclerView.adapter = librosReservadosRVAdapter

        cargarDesdeFirebase()

        librosReservadosRVAdapter.notifyDataSetChanged()

        binding.button.setOnClickListener {
            buscarDesdeFirebase()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buscarDesdeFirebase() {

        val database = FirebaseDatabase.getInstance()
        val myReservasUsuarioRef =
            database.getReference("libros")

        val formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        val fechaActual = LocalDateTime.now().plusDays(2).format(formatters)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val libroServer = dato.getValue(LibroServer::class.java)
                    if (libroServer?.fechaVencimiento == fechaActual) {
                        val user = FirebaseAuth.getInstance()
                        user.let {
                            val uidUsuario = user.uid
                            borrarReservaAuto(uidUsuario, libroServer?.id)
                            Toast.makeText(context, "hecho", Toast.LENGTH_SHORT).show()
                        }
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["estado"] = "Disponible"
                        libroServer?.id.let {
                            myReservasUsuarioRef.child(libroServer?.id.toString())
                                .updateChildren(childUpdates)
                        }
                    }
                }
                librosReservadosRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myReservasUsuarioRef.addListenerForSingleValueEvent(postListener)
    }

    private fun borrarReservaAuto(uidUsuario: String?, idLibro: String?) {
        val database = FirebaseDatabase.getInstance()
        val myUsuariosRef =
            database.getReference("usuarios").child(uidUsuario.toString()).child("reservas")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val reservasUsuarioServer = data.getValue(ReservasUsuarioServer::class.java)
                    reservasUsuarioServer?.let {
                        myUsuariosRef.child(idLibro.toString()).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myUsuariosRef.addListenerForSingleValueEvent(postListener)
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
            myReservasUsuarioRef.addListenerForSingleValueEvent(postListener)
        }
    }

    override fun onItemClick(reservaLibro: ReservasUsuarioServer) {
        val action =
            MisReservasFragmentDirections.actionNavMisReservasToReservasDialogFragment(reservaLibro)
        findNavController().navigate(action)
    }

}