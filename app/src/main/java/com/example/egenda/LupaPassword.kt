package com.example.egenda

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LupaPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        val button = findViewById<ImageButton>(R.id.btnkeluar)
        button.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        val isPasswordVisible = booleanArrayOf(false)
        val eyebtn1 : ImageButton = findViewById(R.id.eyeButton1)
        val etPassword1 : EditText = findViewById(R.id.et_Password1)
        eyebtn1.setOnClickListener {
            togglePasswordVisibility(etPassword1, isPasswordVisible)
        }
        val eyebtn2 : ImageButton = findViewById(R.id.eyeButton2)
        val etPassword2 : EditText = findViewById(R.id.et_password2)
        eyebtn2.setOnClickListener {
            togglePasswordVisibility(etPassword2, isPasswordVisible)
        }

        val etEmail: EditText = findViewById(R.id.et_email)
        val btnSubmit = findViewById<Button>(R.id.btnUbahSandi)
        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val newPassword = etPassword1.text.toString().trim()

            if (email.isNotEmpty() && newPassword.isNotEmpty()) {
                changePassword(email, newPassword)
            } else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isPasswordVisible: BooleanArray) {
        if (isPasswordVisible[0]) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            isPasswordVisible[0] = false
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            isPasswordVisible[0] = true
            editText.setSelection(editText.text.length) // Menjaga posisi kursor setelah mengubah inputType
        }
    }

    private fun changePassword(email: String, newPassword: String) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)  // Replace with your actual base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)
        val call = apiService.gantiPassword(email, newPassword)

        call.enqueue(object : Callback<LupaPasswordResponse> {
            override fun onResponse(call: Call<LupaPasswordResponse>, response: Response<LupaPasswordResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.message?.let { showPopup(it) }
                } else {
                    Toast.makeText(this@LupaPassword, "Gagal mengubah password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LupaPasswordResponse>, t: Throwable) {
                Log.e("API_CALL", "Error: ${t.message}")
                Toast.makeText(this@LupaPassword, "Gagal mengubah password: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPopup(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, login::class.java)
                startActivity(intent)
                finish() // Optionally finish the current activity
            }
        val alert = builder.create()
        alert.show()
    }
}