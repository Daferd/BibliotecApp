package com.daferarevalo.bibliotecapp.ui.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentDetalleLibroBinding


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

        val args: DetalleLibroFragmentArgs by navArgs()
        val libroDetalle = args.libroSeleccionado
        binding.nombreTextView.text = libroDetalle.titulo

    }

}