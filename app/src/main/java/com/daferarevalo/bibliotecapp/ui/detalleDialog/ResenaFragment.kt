package com.daferarevalo.bibliotecapp.ui.detalleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentResenaBinding
import com.google.firebase.auth.FirebaseAuth


class ResenaFragment : Fragment() {

    private lateinit var binding: FragmentResenaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resena, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResenaBinding.bind(view)

        val args: ResenaFragmentArgs by navArgs()
        val libroDetalle = args.libroDetalle

        binding.tituloEventoTextView.text = libroDetalle.titulo

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val nombreUsuario = user.email
            binding.userTextView.text = nombreUsuario.toString()
        }

        binding.enviarButton.setOnClickListener {

        }
    }
}