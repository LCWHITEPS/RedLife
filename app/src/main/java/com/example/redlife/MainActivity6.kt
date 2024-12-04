package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var regreso:Button

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)

        regreso = findViewById(R.id.btn_regresarPerfil)

        regreso.setOnClickListener(){
            var intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }
    }
}