package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var cambioRegistro:Button
        lateinit var cambioInicio:Button

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main7)

        cambioInicio = findViewById(R.id.btn_iniciarSesion)
        cambioRegistro = findViewById(R.id.btn_registro)

        cambioRegistro.setOnClickListener(){

            val intent = Intent(this,MainActivity4::class.java)
            startActivity(intent)
        }

        cambioInicio.setOnClickListener(){
            val intent = Intent(this,MainActivity4::class.java)
            startActivity(intent)
        }
    }
}