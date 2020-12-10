package com.daferarevalo.bibliotecapp.ui.misReservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentReservasDialogBinding
import com.daferarevalo.bibliotecapp.server.LibroServer
import com.daferarevalo.bibliotecapp.server.ReservasUsuarioServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ReservasDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentReservasDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_reservas_dialog, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReservasDialogBinding.bind(view)

        val args: ReservasDialogFragmentArgs by navArgs()
        val reservaLibro = args.reservaSeleccionada

        binding.noButton.setOnClickListener {
            dismiss()
        }

        binding.siButton.setOnClickListener {
            actualizarDatabaseFirebase(reservaLibro.id.toString())
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uidUsuario = user.uid
                borrarDeFaribase(uidUsuario, reservaLibro.id)
            }
            Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun borrarDeFaribase(uidUsuario: String, idLibro: String?) {

        val database = FirebaseDatabase.getInstance()
        val myDeudoresRef =
            database.getReference("usuarios").child(uidUsuario).child("reservas")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val reservasUsuarioServer = data.getValue(ReservasUsuarioServer::class.java)
                    reservasUsuarioServer?.let {
                        myDeudoresRef.child(idLibro.toString()).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myDeudoresRef.addListenerForSingleValueEvent(postListener)
    }

    private fun actualizarDatabaseFirebase(idLibro: String) {
        val database = FirebaseDatabase.getInstance()
        val myLibroRef = database.getReference("libros")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val libroServer = data.getValue(LibroServer::class.java)
                    if (libroServer?.id.equals(idLibro)) {
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["estado"] = "Disponible"
                        idLibro.let { myLibroRef.child(idLibro).updateChildren(childUpdates) }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myLibroRef.addValueEventListener(postListener)
    }
}
