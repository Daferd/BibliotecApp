package com.daferarevalo.bibliotecapp.ui.administrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ActivityAdministradorBottomBinding


class AdministradorBottomActivity : AppCompatActivity() {

    private val reservasFragment = ReservasFragment()
    private val prestamosFragment = PrestamosFragment()


    private lateinit var binding: ActivityAdministradorBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministradorBottomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFregment(reservasFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.reservasFragment -> replaceFregment(reservasFragment)
                R.id.prestamosFragment -> replaceFregment(prestamosFragment)
            }
            true
        }


    }

    private fun replaceFregment(fragment: Fragment) {
        if (fragment != null) {
            val transition = supportFragmentManager.beginTransaction()
            transition.replace(R.id.frame_container, fragment)
            transition.commit()
        }
    }
}