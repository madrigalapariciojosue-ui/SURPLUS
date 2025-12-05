package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class activity_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val flechaRegreso = findViewById<ImageView>(R.id.backArrow)
        flechaRegreso.setOnClickListener {
            finish()
        }

        val emailLoginButton = findViewById<TextView>(R.id.emailLoginButton)
        emailLoginButton.setOnClickListener {
            val intent = Intent(this, sesionCorreo::class.java)
            startActivity(intent)
        }

        val phoneLoginButton = findViewById<TextView>(R.id.phoneLoginButton)
        phoneLoginButton.setOnClickListener {
            val intent = Intent(this, telefono_login::class.java)
            startActivity(intent)
        }
        val googleLoginButton = findViewById<ImageView>(R.id.googleLoginButton)
        googleLoginButton.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión con Google", Toast.LENGTH_SHORT).show()
        }

        val registerText = findViewById<TextView>(R.id.registerText)
        registerText.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }
}}