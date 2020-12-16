package com.daferarevalo.bibliotecapp.ui.administrador.reservas

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentDetalleReservasDialogBinding
import com.daferarevalo.bibliotecapp.server.ReservasServer
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DetalleReservasDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDetalleReservasDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_reservas_dialog, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetalleReservasDialogBinding.bind(view)

        val args: DetalleReservasDialogFragmentArgs by navArgs()
        val detalleReserva = args.detalleReserva

        binding.aprobadoButton.setOnClickListener {
            actualizarEstadoPrestamoFirebase(detalleReserva)
            borrarReservaEnFirebase(detalleReserva)
            Toast.makeText(context, "Aprobada", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.denegadoButton.setOnClickListener {

        }
    }

    private fun borrarReservaEnFirebase(detalleReserva: ReservasServer) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")
            .child(detalleReserva.uidUsuario).child("reservas")

        myUsuarioRef.child(detalleReserva.id.toString()).removeValue()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarEstadoPrestamoFirebase(detalleReserva: ReservasServer) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")

        val formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu")

        val fechaInicial = LocalDateTime.now()
        val fechaFinalAux = fechaInicial.plusDays(20)

        val fechaVencimiento = fechaFinalAux.format(formatters)

        val prestamoServer = ReservasServer(
            detalleReserva.id,
            detalleReserva.titulo,
            detalleReserva.autor,
            fechaVencimiento,
            detalleReserva.imagen,
            detalleReserva.nombreUsuario,
            detalleReserva.uidUsuario
        )

        myUsuarioRef.child(detalleReserva.uidUsuario).child("prestamos")
            .child(detalleReserva.id.toString())
            .setValue(prestamoServer)
    }

}