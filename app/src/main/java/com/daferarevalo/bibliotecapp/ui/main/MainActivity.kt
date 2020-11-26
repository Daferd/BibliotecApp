package com.daferarevalo.bibliotecapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.databinding.ActivityMainBinding
import com.daferarevalo.bibliotecapp.ui.detalleDialog.DetalleDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.text.setOnClickListener {
            val dialog = DetalleDialogFragment()
            dialog.show(supportFragmentManager, "customDialog")
        }

    }
}