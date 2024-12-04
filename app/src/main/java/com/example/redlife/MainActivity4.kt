package com.example.redlife

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity4 : AppCompatActivity() {

    private lateinit var cambioInicio2: Button
    private lateinit var cambioInicioG: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Inicializa botones
        cambioInicio2 = findViewById(R.id.btn_iniciarSesion2)
        cambioInicioG = findViewById(R.id.btn_InicioG)

        // Listener para el botón de iniciar sesión
        cambioInicio2.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Listener para el botón de datos del usuario
        cambioInicioG.setOnClickListener {
            solcitarDatos()
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        crearNotificacion() // Crear canal de notificaciones (para Android 8+)
    }

    private fun solcitarDatos() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_data, null)
        val ageInput = dialogView.findViewById<EditText>(R.id.et_edad)
        val bloodTypeInput = dialogView.findViewById<EditText>(R.id.et_tipoSangre)
        val lastDonationInput = dialogView.findViewById<EditText>(R.id.et_donacion)

        AlertDialog.Builder(this)
            .setTitle("Ingresa lo siguiente")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val age = ageInput.text.toString()
                val bloodType = bloodTypeInput.text.toString()
                val lastDonation = lastDonationInput.text.toString()
                showNotification("Perfil del usuario", "Edad: $age, Tipo de Sangre: $bloodType, Ultima donacion: $lastDonation")
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, "main_channel")
            .setSmallIcon(R.mipmap.logo) // Reemplaza con tu ícono
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificacion_InicioG = NotificationManagerCompat.from(this)
        notificacion_InicioG.notify(1, builder.build())
    }

    private fun crearNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "main_channel",
                "Main Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

