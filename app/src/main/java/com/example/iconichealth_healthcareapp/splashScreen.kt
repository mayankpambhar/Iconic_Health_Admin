package com.example.iconichealth_healthcareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class splashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var firebaseAuth = FirebaseAuth.getInstance()
        var user  = firebaseAuth.currentUser

        Handler().postDelayed(Runnable {
            if (user != null)
            {
                var intent   = Intent(this,Homepage::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                var intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        },3000)
    }
}