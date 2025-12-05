package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class telefono_login : AppCompatActivity() {
    private lateinit var phoneEditText: EditText
    private lateinit var countryCodeLayout: LinearLayout
    private lateinit var countryCodeText: TextView
    private lateinit var flagText: TextView

    // Lista de países con sus códigos y banderas (emojis)
    private val countries = listOf(
        Country("🇲🇽", "México", "+52"),
        Country("🇺🇸", "Estados Unidos", "+1"),
        Country("🇨🇦", "Canadá", "+1"),
        Country("🇪🇸", "España", "+34"),
        Country("🇦🇷", "Argentina", "+54"),
        Country("🇨🇱", "Chile", "+56"),
        Country("🇨🇴", "Colombia", "+57"),
        Country("🇵🇪", "Perú", "+51"),
        Country("🇧🇷", "Brasil", "+55"),
        Country("🇫🇷", "Francia", "+33"),
        Country("🇩🇪", "Alemania", "+49"),
        Country("🇮🇹", "Italia", "+39"),
        Country("🇬🇧", "Reino Unido", "+44")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telefono_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        phoneEditText = findViewById(R.id.phoneEditText)
        countryCodeLayout = findViewById(R.id.countryCodeLayout)
        countryCodeText = findViewById(R.id.countryCodeText)
        flagText = findViewById(R.id.flagText)
    }

    private fun setupListeners() {

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }


        countryCodeLayout.setOnClickListener {
            showCountryCodeDialog()
        }

        findViewById<TextView>(R.id.continueButton).setOnClickListener {
            val phoneNumber = phoneEditText.text.toString()
            val countryCode = countryCodeText.text.toString()

            if (isValidPhoneNumber(phoneNumber)) {

                val fullPhoneNumber = "$countryCode$phoneNumber"
                Toast.makeText(this, "Iniciando sesión con: $fullPhoneNumber", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, Homeactivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor ingresa un número de teléfono válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Texto para registrarse
        findViewById<TextView>(R.id.registerText).setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showCountryCodeDialog() {
        val countryNames = countries.map { "${it.flag} ${it.name} (${it.code})" }

        AlertDialog.Builder(this)
            .setTitle("Selecciona tu país")
            .setItems(countryNames.toTypedArray()) { dialog, which ->
                val selectedCountry = countries[which]
                flagText.text = selectedCountry.flag
                countryCodeText.text = selectedCountry.code
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        return cleanNumber.length >= 10
    }

    data class Country(val flag: String, val name: String, val code: String)
}