package com.example.egenda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.egenda.databinding.ActivityRiwayatAgendaGuruBinding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RiwayatAgendaGuru : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: GuruAdapter
    private lateinit var agendaList: MutableList<DataAgenda>
    private lateinit var binding: ActivityRiwayatAgendaGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAgendaGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        agendaList = mutableListOf()
        adapter = GuruAdapter(agendaList)

        binding.riwayatAgendaList.layoutManager = LinearLayoutManager(this)
        binding.riwayatAgendaList.adapter = adapter

        val guruId = sharedPreferences.getString("user_id", "") ?: ""
        getDataAgenda(guruId)
    }

    private fun getDataAgenda(guruId: String) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(this))
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

        val agendaApi = retrofit.create(UserApi::class.java)
        val call = agendaApi.getAllAgendaList(guruId)

        call.enqueue(object : Callback<GetAllAgenda> {
            override fun onResponse(call: Call<GetAllAgenda>, response: Response<GetAllAgenda>) {
                if (response.isSuccessful) {
                    val allAgenda = response.body()
                    val agendaList = allAgenda?.data ?: emptyList()
                    Log.d("AgendaData", "Jumlah data agenda: ${agendaList.size}")
                    this@RiwayatAgendaGuru.agendaList.clear()
                    this@RiwayatAgendaGuru.agendaList.addAll(agendaList)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@RiwayatAgendaGuru, "Data berhasil diambil", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("AgendaData", "Response tidak berhasil")
                }
            }

            override fun onFailure(call: Call<GetAllAgenda>, t: Throwable) {
                Log.e("AgendaData", "Gagal mengambil data agenda: ${t.message}", t)
            }
        })
    }
}
