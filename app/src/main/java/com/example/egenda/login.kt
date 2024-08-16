package com.example.egenda

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class login : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.et_username)
        emailEditText.maxLines = 1
        emailEditText.setEllipsize(TextUtils.TruncateAt.END)

        val passwordEditText = findViewById<EditText>(R.id.et_password)
        passwordEditText.maxLines = 1
        passwordEditText.setEllipsize(TextUtils.TruncateAt.END)

        val button = findViewById<Button>(R.id.btnLupaPassword)
        button.setOnClickListener {
            val intent = Intent(this, LupaPassword::class.java)
            startActivity(intent)
        }

        val imageButton: ImageButton = findViewById(R.id.imageButton1)
        val editText: EditText = findViewById(R.id.et_password)
        var isPasswordVisible = false
        imageButton.setOnClickListener {
            if (isPasswordVisible) {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                isPasswordVisible = false
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                isPasswordVisible = true
                editText.setSelection(editText.text.length) // Menjaga posisi kursor setelah mengubah inputType
            }
        }

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            redirectToAppropriateScreen()
        } else {
            val emailEditText = findViewById<EditText>(R.id.et_username)
            val passwordEditText = findViewById<EditText>(R.id.et_password)
            val loginButton = findViewById<Button>(R.id.btnmasuk)

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
                } else {
                    loginUser(email, password)
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val userApi = retrofit.create(UserApi::class.java)
        val call = userApi.loginUser(email, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (loginResponse.message == "Login success") {
                            val user = loginResponse.data
                            if (user != null) {
                                saveLoginStatus(user.role, user.name, "", user.phone, user.id.toString(), user.email, loginResponse.token_type, loginResponse.access_token, user.siswa)
                            }
                            redirectToAppropriateScreen()
                            Log.d("LoginActivity", "Login successful. Name: ${user?.name}, User role: ${user?.role}, Email: ${user?.email}")
                        } else {
                            Toast.makeText(this@login, loginResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@login, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Login failed: ${t.message}", t)
                Toast.makeText(this@login, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLoginStatus(
        role: String,
        name: String,
        className: String,
        phone: String,
        id: String,
        email: String,
        token_type: String,
        access_token: String,
        siswa: dataSiswa?
    ) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("role", role)
        editor.putString("name", name)
        editor.putString("className", className)
        editor.putString("phone", phone)
        editor.putString("user_id", id)
        editor.putString("email", email)
        editor.putString("token_type", token_type)
        editor.putString("access_token", access_token)
        if (siswa != null) {
            editor.putInt("siswa_id", siswa.id)
            editor.putString("nis", siswa.nis)
            editor.putInt("kelas_id", siswa.kelas_id)
            editor.putInt("user_id_siswa", siswa.user_id)
            editor.putString("created_at_siswa", siswa.created_at)
            editor.putString("updated_at_siswa", siswa.updated_at)
        }
        editor.apply()
    }

    private fun redirectToAppropriateScreen() {
        val role = sharedPreferences.getString("role", "")
        val intent = if (role == "Guru") {
            Intent(this, BottomNav1::class.java)
        } else {
            Intent(this, BottomNav2::class.java)
        }
        startActivity(intent)
        finish()
    }
}
