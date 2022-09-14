package com.example.iconichealth_healthcareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.iconichealth_healthcareapp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding  : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {
            signin(binding.lemailEdt.text.toString(),binding.lpasswordEdt.text.toString())
        }

    }

    fun signin(email:String,password:String)
    {
        FirebaseApp.initializeApp(this);
        var firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener { res->

                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                var intent = Intent(this,Homepage::class.java)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener {
                    error->
                Log.e("TAG", "signin: ${error.message}", )
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}