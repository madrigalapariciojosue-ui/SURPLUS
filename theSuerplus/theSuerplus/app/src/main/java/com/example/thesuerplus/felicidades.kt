package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class felicidades : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_felicidades)

        findViewById<TextView>(R.id.startButton).setOnClickListener {
            val intent = Intent (this, Homeactivity::class.java)
            startActivity(intent)
            isFinishing()
        }

    }
}