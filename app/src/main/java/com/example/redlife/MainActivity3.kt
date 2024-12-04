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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var regreso:Button
        lateinit var solicitar:Button

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)

        regreso = findViewById(R.id.btn_regresarP)
        solicitar = findViewById(R.id.btn_solicitar)

        regreso.setOnClickListener() {
            var intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        solicitar.setOnClickListener(){
            solicitarDatos()
        }

        crearNotificacion()
    }

    private fun solicitarDatos() {
        val dialogView = layoutInflater.inflate(R.layout.solicitar_donacion, null)
        val tipoSangre = dialogView.findViewById<EditText>(R.id.et_tipoSangreS)
        val cantidadSa = dialogView.findViewById<EditText>(R.id.et_cantidad)
        val lugar = dialogView.findViewById<EditText>(R.id.et_lugar)

        AlertDialog.Builder(this)
            .setTitle("Solicitar donacion")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val bloodType = tipoSangre.text.toString()
                val quantity = cantidadSa.text.toString()
                val location = lugar.text.toString()
                val date = getCurrentDate()
                showNotification(
                    "Solicita",
                    "Tipo de Sangre: $bloodType, Cantidad: $quantity, Lugar: $location, Fecha: $date"
                )
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

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(2, builder.build()) // Cambia el ID para evitar conflictos
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

    private fun getCurrentDate(): String {
        val currentDate = java.util.Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(currentDate)
    }
}
