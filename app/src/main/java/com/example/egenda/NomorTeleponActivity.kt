package com.example.egenda

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NomorTeleponActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomor_telepon)

        val editTextTelefon = findViewById<TextView>(R.id.telefon)
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val telefon = sharedPreferences.getString("phone", "")
        editTextTelefon.setText(telefon)
    }
}