package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Activity_perfil_usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var regreso:Button
        lateinit var nombre:TextView
        lateinit var edad:TextView
        lateinit var tipoSangre:TextView
        lateinit var correo:TextView

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.perfil)

        regreso = findViewById(R.id.btn_regresarPerfil)
        nombre = findViewById(R.id.et_nombreP)
        edad = findViewById(R.id.et_edadP)
        tipoSangre = findViewById(R.id.et_tipoSP)
        correo = findViewById(R.id.et_correoP)

        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("usuarios")
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString() // Reemplazar con el ID del usuario actual

        // Obtener todos los datos del usuario con una sola llamada
        usuariosRef.child(userId).get()
            .addOnSuccessListener { dataSnapshot ->
                val name = dataSnapshot.child("nombre").value?.toString() ?: "Nombre no disponible"
                val age = dataSnapshot.child("edad").value?.toString() ?: "Edad no disponible"
                val sangre = dataSnapshot.child("tipoSangre").value?.toString() ?: "Tipo de sangre no disponible"
                val email = dataSnapshot.child("correo").value?.toString() ?: "Correo no disponible"

                // Asignar valores a las vistas
                nombre.text = name
                edad.text = age
                tipoSangre.text = sangre
                correo.text = email
            }
            .addOnFailureListener {
                // Mostrar errores en las vistas si la operación falla
                nombre.text = "Error al cargar nombre"
                edad.text = "Error al cargar edad"
                tipoSangre.text = "Error al cargar tipo de sangre"
                correo.text = "Error al cargar correo"
            }

        regreso.setOnClickListener(){
            var intent = Intent(this,Activity_inicio_app::class.java)
            startActivity(intent)
        }
    }
}