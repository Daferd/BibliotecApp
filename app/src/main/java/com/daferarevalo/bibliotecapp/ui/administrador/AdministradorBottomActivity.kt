package com.daferarevalo.bibliotecapp.ui.administrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.ActivityAdministradorBottomBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class AdministradorBottomActivity : AppCompatActivity() {

    //private val reservasFragment = ReservasFragment()
    //private val prestamosFragment = PrestamosFragment()


    private lateinit var binding: ActivityAdministradorBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministradorBottomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_frag)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        //replaceFregment(reservasFragment)

        /*binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.reservasFragment -> replaceFregment(reservasFragment)
                R.id.prestamosFragment -> replaceFregment(prestamosFragment)
            }
            true
        }*/


    }

    /*private fun replaceFregment(fragment: Fragment) {
        if (fragment != null) {
            val transition = supportFragmentManager.beginTransaction()
            transition.replace(R.id.frame_container, fragment)
            transition.commit()
        }
    }*/
}