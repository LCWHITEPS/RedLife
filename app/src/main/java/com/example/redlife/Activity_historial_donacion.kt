package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Activity_historial_donacion : AppCompatActivity() {

    private lateinit var regresoH: Button
    private lateinit var donacionesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Esto es para habilitar la visualización de contenido de borde a borde, si lo necesitas
        setContentView(R.layout.historial)

        // Inicialización correcta después de setContentView()
        regresoH = findViewById(R.id.btn_regresarH)
        donacionesContainer = findViewById(R.id.historial)

        regresoH.setOnClickListener {
            val intent = Intent(this, Activity_inicio_app::class.java)
            startActivity(intent)
        }

        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("historialDonaciones")
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString() // Reemplazar con el ID del usuario actual

        usuariosRef.child(userId).get()
            .addOnSuccessListener { dataSnapshot ->
                // Obtener el historial de donaciones
                for (donacionSnapshot in dataSnapshot.children) {
                    // Asumimos que cada donación tiene "fecha", "lugar" y "tipoSangre"
                    val fecha = donacionSnapshot.child("fecha").value?.toString() ?: "Fecha no disponible"
                    val lugar = donacionSnapshot.child("lugar").value?.toString() ?: "Lugar no disponible"
                    val tipoSangre = donacionSnapshot.child("tipoSangre").value?.toString() ?: "Tipo de sangre no disponible"

                    // Crear un nuevo TextView para cada donación
                    val textView = TextView(this)
                    textView.text = "Fecha: $fecha\nLugar: $lugar\nTipo de sangre: $tipoSangre"
                    textView.textSize = 16f
                    textView.setPadding(16, 16, 16, 16)

                    // Agregar el TextView al contenedor
                    donacionesContainer.addView(textView)
                }
            }
            .addOnFailureListener {
                // Mostrar error si la obtención de datos falla
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }

       }

    }







