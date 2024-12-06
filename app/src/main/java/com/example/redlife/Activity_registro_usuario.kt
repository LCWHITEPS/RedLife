package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt


class Activity_registro_usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var cambioRegistro: Button
        lateinit var cambioInicio: Button
        lateinit var et_Nombre: EditText
        lateinit var et_correo: EditText
        lateinit var et_edad: EditText
        lateinit var et_tipoSangre: EditText
        lateinit var et_pass: EditText
        lateinit var et_donacion: EditText
        lateinit var auth: FirebaseAuth
        lateinit var firestore: FirebaseFirestore

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registro)

        cambioInicio = findViewById(R.id.btn_iniciarSesion)
        cambioRegistro = findViewById(R.id.btn_registro)
        et_Nombre = findViewById(R.id.et_nombreCompleto)
        et_correo = findViewById(R.id.et_correoRegistro)
        et_edad = findViewById(R.id.et_edadRegistro)
        et_tipoSangre = findViewById(R.id.et_tipoSangreRegistro)
        et_pass = findViewById(R.id.et_contraRegistro)
        et_donacion = findViewById(R.id.et_donacionSoli)

        cambioInicio.setOnClickListener() {
            val intent = Intent(this, Activity_autenticacion::class.java)
            startActivity(intent)
        }

        cambioRegistro.setOnClickListener() {
            val nombre = et_Nombre.text.toString()
            val correo = et_correo.text.toString()
            val edad = et_edad.text.toString().toIntOrNull() ?: 0 // Convierte a entero, 0 si es inválido
            val tipoSangre = et_tipoSangre.text.toString()
            val password = et_pass.text.toString()
            val donaciones = et_donacion.text.toString().toIntOrNull() ?: 0
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = Usuario(nombre, correo, edad, tipoSangre, donaciones, hashedPassword)

            if (nombre.isNotEmpty() && correo.isNotEmpty() && edad in 18..100 && donaciones > 0 && tipoSangre.isNotEmpty() && password.isNotEmpty()) {
                // Enviar datos a Firebase
                val database = FirebaseDatabase.getInstance()
                val usuariosRef = database.getReference("usuarios")
                val userId = usuariosRef.push().key
                val user = Usuario(
                    nombre,
                    correo,
                    edad,
                    tipoSangre,
                    donaciones,
                    hashedPassword
                ) // Reemplaza con el hash del password si lo necesitas

                auth.createUserWithEmailAndPassword(correo, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                usuariosRef.child(userId).setValue(user)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Usuario registrado con éxito",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent =
                                                Intent(this, Activity_autenticacion::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Error al guardar datos del usuario.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Error al registrar usuario: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        }
    }
}

data class Usuario(

    val nombre: String = "",
    val correo: String = "",
    val edad: Int = 0,
    val tipoSangre: String = "",
    val donaciones: Int = 0,
    val password: String = ""
)