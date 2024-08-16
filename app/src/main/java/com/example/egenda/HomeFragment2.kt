package com.example.egenda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.egenda.databinding.FragmentHome2Binding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment2 : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHome2Binding
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: SiswaAdapter
    private lateinit var agendaList: MutableList<DataAgenda>
    private lateinit var tanggalTextView: TextView
    private lateinit var jumlahDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jumlahDataTextView = view.findViewById(R.id.jumlahDataTextView)

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val namaPenggunaTextView = binding.namaPenggunaTextView2
        val namaPengguna = sharedPreferences.getString("name", "")
        val siswaId = sharedPreferences.getString("user_id", "")
        namaPenggunaTextView.text = namaPengguna
        agendaList = mutableListOf()
        adapter = SiswaAdapter(agendaList)
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView2.adapter = adapter
        swipeRefresh = binding.swipe2
        swipeRefresh.setOnRefreshListener {
            // Trigger data retrieval
            getDataAgenda(siswaId.toString())
        }
        binding.recyclerView2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                swipeRefresh.isEnabled = !recyclerView.canScrollVertically(-1)
            }
        })
        tanggalTextView = binding.tanggalOtomatis2
        setTanggal()

        getDataAgenda(siswaId.toString())
    }

    private fun setTanggal() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val hariFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        val hari = hariFormat.format(calendar.time)
        tanggalTextView.text = "$hari, $formattedDate"
    }

    fun getDataAgenda(siswaId: String) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(requireContext()))
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
        val call = agendaApi.getAllAgendaListSiswa(siswaId)

        call.enqueue(object : Callback<GetAllAgenda> {
            override fun onResponse(call: Call<GetAllAgenda>, response: Response<GetAllAgenda>) {
                if (response.isSuccessful) {
                    val allAgenda = response.body()
                    val agendaList = allAgenda?.data ?: emptyList()
                    Log.d("AgendaData", "Jumlah data agenda: ${agendaList.size}")
                    this@HomeFragment2.agendaList.clear()
                    this@HomeFragment2.agendaList.addAll(agendaList)
                    adapter.notifyDataSetChanged()
                    jumlahDataTextView.text = "${agendaList.size} Kegiatan"
                    swipeRefresh.isRefreshing = false
                    if (isAdded) {
                        context?.let {
                            Toast.makeText(it, "Data berhasil diambil", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    swipeRefresh.isRefreshing = false
                    Log.e("AgendaData", "Response tidak berhasil")
                }
            }

            override fun onFailure(call: Call<GetAllAgenda>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Log.e("AgendaData", "Gagal mengambil data agenda: ${t.message}", t)
            }
        })
    }
}