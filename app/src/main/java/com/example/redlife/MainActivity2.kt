package com.example.redlife

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import android.Manifest
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.maps.OnMapReadyCallback

class MainActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var cambioPerfil:ImageButton
    private lateinit var cambioPubli:ImageButton
    private lateinit var cambioHist:ImageButton

    // Permiso de ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                enableLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        cambioPerfil = findViewById(R.id.btn_perfilUsuario)
        cambioPubli = findViewById(R.id.btn_publicacion)
        cambioHist = findViewById(R.id.btn_historial)

        cambioPerfil.setOnClickListener {
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }

        cambioPubli.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        cambioHist.setOnClickListener{
            val intent = Intent(this, MainActivity7::class.java)
            startActivity(intent)
        }

        // Mapa
        val fragmentoMapa = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fragmentoMapa.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Como sera el mapa
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Comprobar permisos y habilitar la ubicación si es necesario
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Intentar habilitar la ubicación
            try {
                googleMap.isMyLocationEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            // Solicitar el permiso de ubicación
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    // Método para habilitar la ubicación del usuario en el mapa
    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                googleMap.isMyLocationEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
            } catch (e: SecurityException) {
                // Manejar la excepción en caso de que el permiso no sea concedido o se revocó
                e.printStackTrace()
            }
        } else {
            // Solicitar permiso si no está disponible
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}