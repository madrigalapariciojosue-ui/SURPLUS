package com.example.thesuerplus

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.util.Patterns

class registro : AppCompatActivity() {

    // Views del layout
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var nombreEditText: EditText
    private lateinit var checkLength: ImageView
    private lateinit var checkUppercase: ImageView
    private lateinit var checkNumber: ImageView
    private lateinit var passwordVisibility: ImageView
    private lateinit var continueButton: TextView

    // Variables de estado
    private var isPasswordVisible = false

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        auth = Firebase.auth
        configurarGoogleSignIn()
        initViews()
        setupListeners()
    }

    private fun initViews() {
        passwordEditText = findViewById(R.id.passwordEditText)
        emailEditText = findViewById(R.id.emailEditText)
        nombreEditText = findViewById(R.id.nombreEditText)
        checkLength = findViewById(R.id.checkLength)
        checkUppercase = findViewById(R.id.checkUppercase)
        checkNumber = findViewById(R.id.checkNumber)
        passwordVisibility = findViewById(R.id.passwordVisibility)
        continueButton = findViewById(R.id.continueButton)
    }

    private fun configurarGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        passwordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }


        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })


        continueButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val nombre = nombreEditText.text.toString().trim()

            if (isValidForm(email, password, nombre)) {
                registrarUsuario(email, password, nombre)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.googleSignUpButton).setOnClickListener {
            iniciarSesionGoogle()
        }

        findViewById<TextView>(R.id.loginLinkText).setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
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

    private fun validatePassword(password: String) {
        // Validar 8carateres
        val hasMinLength = password.length >= 8
        updateCheckIcon(checkLength, hasMinLength)

        // Validar mayúsculas
        val hasUppercase = password.any { it.isUpperCase() }
        updateCheckIcon(checkUppercase, hasUppercase)

        // Validar números
        val hasNumber = password.any { it.isDigit() }
        updateCheckIcon(checkNumber, hasNumber)
    }

    private fun updateCheckIcon(imageView: ImageView, isValid: Boolean) {
        if (isValid) {
            // Icono verde (válido)
            imageView.setImageResource(android.R.drawable.presence_online)
            imageView.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            // Icono rojo (inválido)
            imageView.setImageResource(android.R.drawable.presence_offline)
            imageView.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }

    private fun isValidForm(email: String, password: String, nombre: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isNombreValid = nombre.isNotEmpty()

        return isEmailValid && isNombreValid && hasMinLength && hasUppercase && hasNumber
    }


    private fun registrarUsuario(email: String, password: String, nombre: String) {
        // Deshabilitar botón durante el registro
        continueButton.isEnabled = false
        continueButton.text = "Registrando..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    actualizarPerfilUsuario(user, nombre)
                    enviarEmailVerificacion(user)
                } else {
                    // Rehabilitar botón en caso de error
                    continueButton.isEnabled = true
                    continueButton.text = "Continuar"
                    Toast.makeText(
                        this,
                        "Error en el registro: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun actualizarPerfilUsuario(user: FirebaseUser?, nombre: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(nombre)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun enviarEmailVerificacion(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                // Rehabilitar botón
                continueButton.isEnabled = true
                continueButton.text = "Continuar"

                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "¡Registro exitoso! Se ha enviado un email de verificación.",
                        Toast.LENGTH_LONG
                    ).show()


                    val intent = Intent(this, telefono::class.java)
                    intent.putExtra("email", user.email)
                    intent.putExtra("nombre", user.displayName)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error al enviar email de verificación: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun iniciarSesionGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    autenticarConFirebase(token)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en Google Sign-In: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticarConFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Registro con Google exitoso", Toast.LENGTH_SHORT).show()

                    redirigirAMainActivity()
                } else {
                    Toast.makeText(this, "Error en autenticación con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun redirigirAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            redirigirAMainActivity()
        }
    }
}