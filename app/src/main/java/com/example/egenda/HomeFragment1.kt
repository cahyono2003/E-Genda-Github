package com.example.egenda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.egenda.databinding.FragmentHome1Binding
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

class HomeFragment1 : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHome1Binding
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: GuruAdapter
    private lateinit var agendaList: MutableList<DataAgenda>
    private lateinit var tanggalTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val refreshTimeout = 5000L
    private lateinit var jumlahDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jumlahDataTextView = view.findViewById(R.id.jumlahDataTextView)

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val namaPenggunaTextView = binding.namaPenggunaTextView1
        val namaPengguna = sharedPreferences.getString("name", "")
        val guruId = sharedPreferences.getString("user_id", "")
        namaPenggunaTextView.text = namaPengguna
        agendaList = mutableListOf()
        adapter = GuruAdapter(agendaList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        swipeRefresh = binding.swipe
        swipeRefresh.setOnRefreshListener {
            getDataAgenda(guruId.toString())
            handler.postDelayed({
                if(swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), "Gagal menyegarkan data", Toast.LENGTH_SHORT).show()
                }
            }, refreshTimeout)
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                swipeRefresh.isEnabled = !recyclerView.canScrollVertically(-1)
            }
        })

        tanggalTextView = binding.tanggalOtomatis1
        setTanggal()

        getDataAgenda(guruId.toString())
    }

    private fun setTanggal() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val hariFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        val hari = hariFormat.format(calendar.time)
        tanggalTextView.text = "$hari, $formattedDate"
    }

    private fun getDataAgenda(guruId: String) {
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
        val call = agendaApi.getAllAgendaList(guruId)

        call.enqueue(object : Callback<GetAllAgenda> {
            override fun onResponse(call: Call<GetAllAgenda>, response: Response<GetAllAgenda>) {
                if (response.isSuccessful) {
                    val allAgenda = response.body()
                    val agendaList = allAgenda?.data ?: emptyList()
                    Log.d("AgendaData", "Jumlah data agenda: ${agendaList.size}")
                    this@HomeFragment1.agendaList.clear()
                    this@HomeFragment1.agendaList.addAll(agendaList)
                    jumlahDataTextView.text = "${agendaList.size} Kegiatan"
                    adapter.notifyDataSetChanged()
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
