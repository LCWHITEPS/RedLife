package com.example.redlife

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity7 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var regresoH:Button

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main6)

        regresoH = findViewById(R.id.btn_regresarH)

        regresoH.setOnClickListener(){
            var intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }

    }
}