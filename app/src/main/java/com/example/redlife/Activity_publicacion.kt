package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Activity_publicacion : AppCompatActivity() {

    private lateinit var solicitar: Button
    private lateinit var donar: Button
    private lateinit var regreso:Button
    private lateinit var solicitudesContainer: LinearLayout
    private var solicitudSeleccionada: Solicitud? = null
    private val solicitudesTemporales = mutableListOf<Solicitud>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publicacion)

        // Cassssar
        solicitar = findViewById(R.id.btn_solicitar)
        donar = findViewById(R.id.btn_donar)
        solicitudesContainer = findViewById(R.id.solicitudesContainer)
        regreso = findViewById(R.id.btn_regresarP)

        // Asignar listeners
        solicitar.setOnClickListener {
            mostrarDialogoSolicitud()
        }

        donar.setOnClickListener {
            realizarDonacion()
        }

        regreso.setOnClickListener(){
            var intent = Intent(this,Activity_inicio_app::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDialogoSolicitud() {
        val dialogView = layoutInflater.inflate(R.layout.solicitar_donacion, null)
        val tipoSangre = dialogView.findViewById<EditText>(R.id.et_tipoSangreS)
        val cantidadSa = dialogView.findViewById<EditText>(R.id.et_cantidad)
        val lugar = dialogView.findViewById<EditText>(R.id.et_lugar)

        AlertDialog.Builder(this)
            .setTitle("Solicitar Donación")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val bloodType = tipoSangre.text.toString()
                val quantity = cantidadSa.text.toString()
                val location = lugar.text.toString()

                if (bloodType.isEmpty() || quantity.isEmpty() || location.isEmpty()) {
                    Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    agregarSolicitud(bloodType, quantity, location)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun agregarSolicitud(tipoSangre: String, cantidad: String, lugar: String) {
        val nuevaSolicitud = Solicitud(tipoSangre, cantidad, lugar)

        // Añadir la solicitud a la lista temporal
        solicitudesTemporales.add(nuevaSolicitud)

        // Crear un CheckBox para mostrar la solicitud
        val solicitudCheckBox = CheckBox(this).apply {
            text = "Tipo de Sangre: $tipoSangre\nCantidad: $cantidad\nLugar: $lugar"
            textSize = 16f
            setPadding(8, 8, 8, 8)
        }

        // Al seleccionar el CheckBox, marcar como seleccionada
        solicitudCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var solicitudSeleccionada = nuevaSolicitud
            } else if (solicitudSeleccionada == nuevaSolicitud) {
                solicitudSeleccionada = null
            }
        }

        solicitudesContainer.addView(solicitudCheckBox)
    }


    private fun realizarDonacion() {
        // Obtener la solicitud seleccionada
        val seleccionada = solicitudesContainer.children
            .filterIsInstance<CheckBox>()
            .firstOrNull { it.isChecked }

        if (seleccionada == null) {
            Toast.makeText(this, "Por favor selecciona una solicitud", Toast.LENGTH_SHORT).show()
            return
        }

        // Encontrar la solicitud en la lista
        val index = solicitudesContainer.indexOfChild(seleccionada)
        val solicitud = solicitudesTemporales.getOrNull(index)

        if (solicitud != null) {
            verificarCompatibilidad(solicitud)

            // Guardar la fecha de la donación en Firebase
            val database = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid.toString()

            // Crear la referencia al historial de donaciones del usuario
            val historialRef = database.getReference("historialDonaciones").child(userId)

            // Obtener la fecha actual
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            // Crear un objeto de donación con la fecha
            val donacion = Donacion(fechaActual, solicitud.tipoSangre, solicitud.cantidad, solicitud.lugar, userId)

            // Guardar la donación en el historial
            historialRef.push().setValue(donacion).addOnSuccessListener {
                Toast.makeText(this, "Donación registrada con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar la donación", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Error al encontrar la solicitud seleccionada", Toast.LENGTH_SHORT).show()
        }
    }


    private fun verificarCompatibilidad(solicitud: Solicitud) {
        obtenerTipoSangre { tipoDonante ->
            if (tipoDonante.isNotEmpty() && tipoDonante != "Error") {
                if (esCompatible(tipoDonante, solicitud.tipoSangre)) {
                    incrementarDonacion()
                } else {
                    Toast.makeText(this, "Tu tipo de sangre no es compatible", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al obtener tu tipo de sangre", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun incrementarDonacion() {
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()

        val usuarioRef = database.getReference("usuarios").child(userId)

        usuarioRef.child("donaciones").get().addOnSuccessListener { dataSnapshot ->
            val donacionesActuales = dataSnapshot.getValue(Int::class.java) ?: 0
            usuarioRef.child("donaciones").setValue(donacionesActuales + 1)
            Toast.makeText(this, "Gracias por tu donación", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al actualizar las donaciones", Toast.LENGTH_SHORT).show()
        }
    }

    fun obtenerTipoSangre(callback: (String) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("usuarios")
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()

        usuariosRef.child(userId).get().addOnSuccessListener { dataSnapshot ->
            val tipoSangre = dataSnapshot.child("tipoSangre").value.toString()
            callback(tipoSangre)
        }.addOnFailureListener {
            callback("Error")
        }
    }

    fun esCompatible(tipoDonante: String, tipoReceptor: String): Boolean {
        return when (tipoReceptor) {
            "A+" -> tipoDonante in listOf("A+", "A-", "O+", "O-")
            "A-" -> tipoDonante in listOf("A-", "O-")
            "B+" -> tipoDonante in listOf("B+", "B-", "O+", "O-")
            "B-" -> tipoDonante in listOf("B-", "O-")
            "AB+" -> tipoDonante in listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
            "AB-" -> tipoDonante in listOf("A-", "B-", "AB-", "O-")
            "O+" -> tipoDonante in listOf("O+", "O-")
            "O-" -> tipoDonante == "O-"
            else -> false
        }
    }
}

data class Solicitud(
    val tipoSangre: String,
    val cantidad: String,
    val lugar: String
)

data class Donacion(
    val fecha: String,
    val tipoSangre: String,
    val cantidad: String,
    val lugar: String,
    val userId: String
)



