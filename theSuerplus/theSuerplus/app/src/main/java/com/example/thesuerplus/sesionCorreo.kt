package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class sesionCorreo : AppCompatActivity() {
    private lateinit var emailEditText: EditText

    private lateinit var passwordEditText: EditText
    private lateinit var passwordVisibility: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesion_correo)

        initViews()

        setupListeners()
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordVisibility = findViewById(R.id.passwordVisibility)
    }

    private fun setupListeners() {

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }


        passwordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }


        findViewById<TextView>(R.id.forgotPasswordText).setOnClickListener {

            Toast.makeText(this, "Funcionalidad de recuperación de contraseña", Toast.LENGTH_SHORT).show()

        }


        findViewById<TextView>(R.id.loginButton).setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (isValidForm(email, password)) {

                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Homeactivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }


        findViewById<TextView>(R.id.registerText).setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {

            passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordVisibility.setImageResource(android.R.drawable.ic_menu_view)
        } else {

            passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordVisibility.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        }
        isPasswordVisible = !isPasswordVisible

        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun isValidForm(email: String, password: String): Boolean {

        return email.isNotEmpty() && password.isNotEmpty()
        }
}