package com.example.redlife

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Activity_autenticacion : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    lateinit var cambioInicio2: Button
    lateinit var cambioInicioG: Button
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Inicialización de vistas
        email = findViewById(R.id.et_correoInicioSesion)
        pass = findViewById(R.id.et_passInicioSesion)
        cambioInicio2 = findViewById(R.id.btn_iniciarSesion2)
        cambioInicioG = findViewById(R.id.btn_InicioG)

        val googleSignInOptions = com.google.android.gms.auth.api.signin.GoogleSignInOptions
            .Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Obtén este ID de la consola de Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Listener para el botón de inicio de sesión con Google
        cambioInicioG.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Listener para el botón de iniciar sesión con correo y contraseña
        cambioInicio2.setOnClickListener {
            val email = email.text.toString()
            val password = pass.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Activity_inicio_app::class.java))
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la autenticación con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Autenticar con Firebase usando el token de Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        account?.let {
            val credential = GoogleAuthProvider.getCredential(it.idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        val isFirstLogin = sharedPreferences.getBoolean("PrimerLogin", true)

                        if (isFirstLogin) {
                            // Muestra el mensaje de bienvenida único y solicita datos
                            Toast.makeText(this, "Bienvenido ${user?.displayName}!", Toast.LENGTH_LONG).show()
                            mostrarDialogoSolicitud()
                            // Marca que ya no es el primer inicio
                            sharedPreferences.edit().putBoolean("PrimerLogin", false).apply()
                        } else {
                            // Redirige si ya no es el primer inicio
                            startActivity(Intent(this, Activity_inicio_app::class.java))
                        }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun mostrarDialogoSolicitud() {
        val dialogView = layoutInflater.inflate(R.layout.solicitar_datos_faltantes, null)
        val nombre = dialogView.findViewById<EditText>(R.id.et_nombreSoli)
        val correo = dialogView.findViewById<EditText>(R.id.et_correoSoli)
        val edad = dialogView.findViewById<EditText>(R.id.et_edadSoli)
        val tipo = dialogView.findViewById<EditText>(R.id.et_tipoSangreSoli)
        val donacion = dialogView.findViewById<EditText>(R.id.et_donacionSoli)

        val user = mAuth.currentUser
        val googleEmail = user?.email
        if (googleEmail != null) {
            correo.setText(googleEmail)
        }

        AlertDialog.Builder(this)
            .setTitle("Solicitar datos")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nombre.text.toString()
                val email = correo.text.toString()
                val age = edad.text.toString().toIntOrNull() ?: 0
                val bloodType = tipo.text.toString()
                val don = donacion.text.toString().toIntOrNull() ?: 0

                if (name.isNotEmpty() && email.isNotEmpty() && age in 18..100 && don > 0 && bloodType.isNotEmpty()) {
                    // Enviar datos a Firebase
                    val database = FirebaseDatabase.getInstance()
                    val usuariosRef = database.getReference("usuarios")
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    val user = Usuario(
                        name,
                        email,
                        age,
                        bloodType,
                        don
                    )
                    usuariosRef.child(userId!!).setValue(user)
                    startActivity(Intent(this, Activity_inicio_app::class.java))
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
}
