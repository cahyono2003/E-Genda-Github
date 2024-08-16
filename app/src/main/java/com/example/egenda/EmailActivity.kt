package com.example.egenda

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        val editTextEmail = findViewById<TextView>(R.id.email)
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        editTextEmail.setText(email)
    }
}