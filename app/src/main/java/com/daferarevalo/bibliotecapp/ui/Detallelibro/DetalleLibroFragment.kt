package com.daferarevalo.bibliotecapp.ui.Detallelibro

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentDetalleLibroBinding
import com.squareup.picasso.Picasso


class DetalleLibroFragment : Fragment() {

    private lateinit var binding: FragmentDetalleLibroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_libro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDetalleLibroBinding.bind(view)

        val medidasVentana: DisplayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(medidasVentana)

        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels

        val nuevoAncho = ancho * 0.85
        val nuevoAlto = alto * 0.6

        activity?.window?.setLayout(nuevoAncho.toInt(), nuevoAlto.toInt())

        val args: DetalleLibroFragmentArgs by navArgs()
        val libroDetalle = args.libroSeleccionado
        binding.tituloTextView.text = libroDetalle.titulo
        binding.autorTextView.text = libroDetalle.autor
        binding.categoriaTextView.text = libroDetalle.categoria
        binding.signaturaTextView.text = libroDetalle.signatura
        binding.estadoTextView.text = libroDetalle.estado
        Picasso.get().load(libroDetalle.imagen).into(binding.librosImageView)

    }

}