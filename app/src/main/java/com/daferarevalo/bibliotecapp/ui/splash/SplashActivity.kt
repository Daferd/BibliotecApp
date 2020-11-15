package com.daferarevalo.bibliotecapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.ui.drawer.DrawerActivity
import com.daferarevalo.bibliotecapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val timer = Timer()
        timer.schedule(
            timerTask {
                val auth = FirebaseAuth.getInstance()
                if (auth.uid == null) {
                    goToLoginActivity()
                } else {
                    goToDrawerActivity()
                }
            }, 1000
        )
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToDrawerActivity() {
        val intent = Intent(this, DrawerActivity::class.java)
        startActivity(intent)
        finish()
    }
}