package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val guestTextView = findViewById<TextView>(R.id.guestTextView)

        loginButton.setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }

        guestTextView.setOnClickListener {
            Toast.makeText(this, "Entrando como invitado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Homeactivity::class.java)
            startActivity(intent)
        }
    }
}