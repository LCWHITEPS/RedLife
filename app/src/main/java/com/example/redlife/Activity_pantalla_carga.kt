package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class Activity_pantalla_carga : AppCompatActivity() {

    lateinit var cambio:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantalla_carga)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,Activity_registro_usuario::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
