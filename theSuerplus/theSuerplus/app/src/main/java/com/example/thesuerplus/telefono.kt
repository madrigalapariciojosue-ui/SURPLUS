package com.example.thesuerplus


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class telefono : AppCompatActivity() {


    private lateinit var phoneEditText: EditText
    private lateinit var countryCodeLayout: LinearLayout
    private lateinit var countryCodeText: TextView
    private lateinit var flagText: TextView
    private lateinit var confirmButton: TextView
    private lateinit var verifyButton: TextView
    private lateinit var verificationCodeEditText: EditText
    private lateinit var verificationLayout: LinearLayout
    private lateinit var timerText: TextView
    private lateinit var resendCodeText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var warningText: TextView

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var countDownTimer: CountDownTimer? = null
    private var isResendEnabled = false

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
    companion object {
        private const val RESEND_TIMEOUT = 60000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telefono)
        auth = Firebase.auth
        initViews()
        setupListeners()
    }

    private fun initViews() {
        phoneEditText = findViewById(R.id.phoneEditText)
        countryCodeLayout = findViewById(R.id.countryCodeLayout)
        countryCodeText = findViewById(R.id.countryCodeText)
        flagText = findViewById(R.id.flagText)
        confirmButton = findViewById(R.id.confirmButton)
        verifyButton = findViewById(R.id.verifyButton)
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText)
        verificationLayout = findViewById(R.id.verificationLayout)
        timerText = findViewById(R.id.timerText)
        resendCodeText = findViewById(R.id.resendCodeText)
        progressBar = findViewById(R.id.progressBar)
        warningText = findViewById(R.id.warningText)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }
        countryCodeLayout.setOnClickListener {
            showCountryCodeDialog()
        }
        confirmButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString()
            val countryCode = countryCodeText.text.toString()

            if (isValidPhoneNumber(phoneNumber)) {
                val fullPhoneNumber = "$countryCode$phoneNumber"
                sendVerificationCode(fullPhoneNumber)
            } else {
                Toast.makeText(this, "Por favor ingresa un número de teléfono válido", Toast.LENGTH_SHORT).show()
            }
        }

        verifyButton.setOnClickListener {
            val code = verificationCodeEditText.text.toString().trim()
            if (code.length == 6) {
                verifyPhoneNumberWithCode(code)
            } else {
                Toast.makeText(this, "Ingresa un código válido de 6 dígitos", Toast.LENGTH_SHORT).show()
            }
        }

        // Reenviar código
        resendCodeText.setOnClickListener {
            if (isResendEnabled) {
                val phoneNumber = phoneEditText.text.toString()
                val countryCode = countryCodeText.text.toString()
                if (isValidPhoneNumber(phoneNumber)) {
                    val fullPhoneNumber = "$countryCode$phoneNumber"
                    resendVerificationCode(fullPhoneNumber)
                }
            }
        }

        findViewById<TextView>(R.id.loginText).setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
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
        if (cleanNumber.length < 10) {
            Toast.makeText(this, "El número debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cleanNumber.all { it == '0' }) {
            Toast.makeText(this, "Ingresa un número válido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun sendVerificationCode(phoneNumber: String) {
        showLoading(true)
        confirmButton.isClickable = false

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(verificationCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phoneNumber: String) {
        showLoading(true)
        resendCodeText.isClickable = false

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(verificationCallbacks)
            .setForceResendingToken(resendToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            showLoading(false)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            showLoading(false)
            confirmButton.isClickable = true

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    warningText.text = "Número de teléfono inválido"
                    Toast.makeText(this@telefono, "Número de teléfono inválido", Toast.LENGTH_LONG).show()
                }
                is FirebaseTooManyRequestsException -> {
                    warningText.text = "Demasiados intentos. Intenta más tarde."
                    Toast.makeText(this@telefono, "Límite de intentos excedido. Intenta más tarde.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    warningText.text = "Error: ${e.message}"
                    Toast.makeText(this@telefono, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            showLoading(false)

            storedVerificationId = verificationId
            resendToken = token

            // Cambiar UI para mostrar campo de verificación
            verificationLayout.visibility = android.view.View.VISIBLE
            confirmButton.visibility = android.view.View.GONE
            phoneEditText.isEnabled = false
            countryCodeLayout.isClickable = false

            startCountdownTimer()

            Toast.makeText(this@telefono, "Código enviado por SMS", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCountdownTimer() {
        countDownTimer?.cancel()

        timerText.text = "Puedes reenviar en 60 segundos"
        resendCodeText.isClickable = false
        resendCodeText.setTextColor(resources.getColor(android.R.color.darker_gray))
        isResendEnabled = false

        countDownTimer = object : CountDownTimer(RESEND_TIMEOUT, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerText.text = "Puedes reenviar en $seconds segundos"
            }

            override fun onFinish() {
                timerText.text = "¿No recibiste el código?"
                resendCodeText.isClickable = true
                resendCodeText.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
                isResendEnabled = true
            }
        }.start()
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        storedVerificationId?.let { verificationId ->
            showLoading(true)
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } ?: run {
            Toast.makeText(this, "Primero solicita un código", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Vincular teléfono al usuario existente
            currentUser.linkWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    showLoading(false)

                    if (task.isSuccessful) {
                        // Teléfono vinculado exitosamente
                        handlePhoneVerificationSuccess()
                    } else {
                        // Si falla el link, intentar signIn normal
                        handlePhoneSignIn(credential)
                    }
                }
        } else {

            handlePhoneSignIn(credential)
        }
    }

    private fun handlePhoneSignIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)

                if (task.isSuccessful) {

                    handlePhoneVerificationSuccess()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Código de verificación inválido", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun handlePhoneVerificationSuccess() {
        val phoneNumber = phoneEditText.text.toString()
        val countryCode = countryCodeText.text.toString()
        val fullPhoneNumber = "$countryCode$phoneNumber"

        savePhoneNumberToUserProfile(fullPhoneNumber)

        Toast.makeText(this, "¡Teléfono verificado exitosamente!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, felicidades::class.java)
        startActivity(intent)
        finish()
    }

    private fun savePhoneNumberToUserProfile(phoneNumber: String) {
        val user = auth.currentUser
        if (user != null) {
            // Aquí puedes guardar el teléfono en Firestore o Realtime Database
            // Por ejemplo, usando Firestore:
            /*
            val db = Firebase.firestore
            val userData = hashMapOf(
                "phoneNumber" to phoneNumber,
                "phoneVerified" to true
            )

            db.collection("users").document(user.uid)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("PhoneAuth", "Teléfono guardado en Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("PhoneAuth", "Error al guardar teléfono", e)
                }
            */

            val profileUpdates = UserProfileChangeRequest.Builder()
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Teléfono guardado: $phoneNumber", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        if (show) {
            confirmButton.text = "Enviando..."
            verifyButton.text = "Verificando..."
        } else {
            confirmButton.text = "Enviar código"
            verifyButton.text = "Verificar código"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    data class Country(val flag: String, val name: String, val code: String)
}