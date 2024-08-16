package com.example.egenda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class AproveAgenda : AppCompatActivity() {

    private lateinit var btnTambahAbsensi: Button
    private lateinit var recyclerViewAbsensi: RecyclerView
    private lateinit var textDataAbsen: TextView
    private lateinit var tanggal: TextView
    private lateinit var kelas: TextView
    private lateinit var mataPelajaran: TextView
    private lateinit var kompetensiDasar: TextView
    private lateinit var kegiatanPembelajaran: TextView
    private lateinit var request: TextView
    private lateinit var statusAprove: Button
    private lateinit var jadwalMulai: TextView
    private lateinit var jadwalSelesai: TextView
    private lateinit var absensiAdapter: AbsenAdapter
    private val absensiList = mutableListOf<Absensi>()
    private lateinit var siswaList: List<SiswaData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprove_agenda)

        btnTambahAbsensi = findViewById(R.id.btnTambahAbsensi)
        btnTambahAbsensi.setOnClickListener {
            tambahAbsensi()
        }

        statusAprove = findViewById(R.id.statusAprove1)
        statusAprove.setOnClickListener {
            approveAgenda()
        }

        textDataAbsen = findViewById(R.id.DataAbsen)

        val agendaItem = intent.getParcelableExtra<DataAgenda>("agenda")

        textDataAbsen = findViewById(R.id.DataAbsen)
        tanggal = findViewById(R.id.tanggal)
        kelas = findViewById(R.id.Kelas)
        mataPelajaran = findViewById(R.id.mataPelajaran)
        kompetensiDasar = findViewById(R.id.kompetensidasar)
        kegiatanPembelajaran = findViewById(R.id.kegiatanPembelajaran)
        request = findViewById(R.id.Request)
        jadwalMulai = findViewById(R.id.jamMulai)
        jadwalSelesai = findViewById(R.id.jamSelesai)
        statusAprove = findViewById(R.id.statusAprove1)
        recyclerViewAbsensi = findViewById(R.id.absenItem1)
        recyclerViewAbsensi.layoutManager = LinearLayoutManager(this)
        absensiAdapter = AbsenAdapter(absensiList)
        recyclerViewAbsensi.adapter = absensiAdapter

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tanggal.text = formattedDate

        kelas.text = agendaItem?.kelas
        mataPelajaran.text = agendaItem?.mata_pelajaran
        request.text = agendaItem?.nama_guru
        jadwalMulai.text = agendaItem?.jadwal_mulai
        jadwalSelesai.text = agendaItem?.jadwal_selesai
        kompetensiDasar.text = agendaItem?.kompetensi_dasar
        kegiatanPembelajaran.text = agendaItem?.kegiatan_belajar

        if (agendaItem?.approved_data!!.isNotEmpty()) {
            statusAprove.text = "Approved"
            statusAprove.background = ContextCompat.getDrawable(this, R.drawable.shapeeditext)
        } else {
            statusAprove.text = "Approve"
        }

        loadAbsensiData(agendaItem)
    }

    private fun loadAbsensiData(agendaItem: DataAgenda) {
        absensiList.clear()
        absensiList.addAll(agendaItem.absensi)
        absensiAdapter.notifyDataSetChanged()
    }

    private fun approveAgenda() {
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

        val apiService = retrofit.create(UserApi::class.java)
        val siswaId = getCurrentUserId()
        val signature = "signatures_approvedSiswa/paraf_siswa.png"
        val formattedDate = tanggal.text.toString()
        val call = apiService.approveAgenda(formattedDate, siswaId, signature)

        call.enqueue(object : Callback<ApproveAgendaResponse> {
            override fun onResponse(call: Call<ApproveAgendaResponse>, response: Response<ApproveAgendaResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        Toast.makeText(this@AproveAgenda, "Data berhasil diapprove", Toast.LENGTH_SHORT).show()
                        Log.d("AproveAgenda", "Status: ${it.status}, Message: ${it.message}")

                        // Pindah ke halaman BottomNav2 setelah berhasil approve
                        val intent = Intent(this@AproveAgenda, BottomNav2::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@AproveAgenda, "Request failed with code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("AproveAgenda", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApproveAgendaResponse>, t: Throwable) {
                Toast.makeText(this@AproveAgenda, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AproveAgenda", "Request failed: ${t.message}")
            }
        })
    }

    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("siswa_id", 0)
    }

    private fun tambahAbsensi() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog1)
        builder.setTitle("Tambah Absensi")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 20, 25, 18)
        }

        val heightInDp = 40
        val scale = resources.displayMetrics.density
        val heightInPx = (heightInDp * scale).toInt()

        val spinnerNama = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                heightInPx
            ).apply {
                setMargins(0, 0, 0, 20)
            }
            setBackgroundResource(R.drawable.spinner_background)
            setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        }
        layout.addView(spinnerNama)

        val radioGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setPadding(0, 20, 0, 0)
        }

        val radioButton1 = RadioButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = "Izin"
            setTextColor(ContextCompat.getColorStateList(this@AproveAgenda, R.color.radio_button_text_color))
            id = View.generateViewId()
        }

        val radioButton2 = RadioButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = "Sakit"
            setTextColor(ContextCompat.getColorStateList(this@AproveAgenda, R.color.radio_button_text_color))
            id = View.generateViewId()
        }

        val radioButton3 = RadioButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = "Tanpa Keterangan"
            setTextColor(ContextCompat.getColorStateList(this@AproveAgenda, R.color.radio_button_text_color))
            id = View.generateViewId()
        }

        radioGroup.addView(radioButton1)
        radioGroup.addView(radioButton2)
        radioGroup.addView(radioButton3)
        layout.addView(radioGroup)

        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val nama = spinnerNama.selectedItem.toString()
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val radioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val status = radioButton.text.toString()
            val siswaId = getSiswaIdByName(siswaList, nama)

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val waktuAbsen = dateFormat.format(calendar.time)
            val tanggalAgenda = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            simpanAbsensi(siswaId.toString(), status, waktuAbsen, "", tanggalAgenda)

            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()

        loadSiswaData(spinnerNama, null)
    }

    private fun simpanAbsensi(siswaId: String, status: String, waktuAbsen: String, keterangan: String, tanggalAgenda: String) {
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

        val apiService = retrofit.create(UserApi::class.java)
        val call = apiService.simpanAbsensi(0, status, waktuAbsen, keterangan, tanggalAgenda, siswaId)

        call.enqueue(object : Callback<Absen> {
            override fun onResponse(call: Call<Absen>, response: Response<Absen>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        Toast.makeText(this@AproveAgenda, it.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AproveAgenda, "Gagal menyimpan absensi: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Absen>, t: Throwable) {
                Toast.makeText(this@AproveAgenda, "Gagal menyimpan absensi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSiswaIdByName(siswaList: List<SiswaData>, name: String): Int {
        for (siswa in siswaList) {
            val user = siswa.user
            if (user.name == name) {
                return siswa.id
            }
        }
        return -1
    }

    private fun loadSiswaData(spinner: Spinner, defaultNama: String?) {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val kelasId = sharedPreferences.getInt("kelas_id", -1)

        if (kelasId == -1) {
            Toast.makeText(this, "Gagal memuat data: kelas_id tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

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

        val apiService = retrofit.create(UserApi::class.java)
        val call = apiService.getSiswa(kelasId.toString())

        call.enqueue(object : Callback<GetSiswaByKelas> {
            override fun onResponse(call: Call<GetSiswaByKelas>, response: Response<GetSiswaByKelas>) {
                if (response.isSuccessful) {
                    val getSiswaByKelas = response.body()
                    if (getSiswaByKelas != null) {
                        siswaList = getSiswaByKelas.data.siswa
                        val siswaNames = siswaList.map { it.user.name }

                        // Atur data ke Spinner
                        val adapter = ArrayAdapter(this@AproveAgenda, android.R.layout.simple_spinner_item, siswaNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        // Set nilai default jika ada
                        defaultNama?.let {
                            val defaultPosition = siswaNames.indexOf(it)
                            if (defaultPosition >= 0) {
                                spinner.setSelection(defaultPosition)
                            }
                        }
                    } else {
                        Toast.makeText(this@AproveAgenda, "Response body null atau data siswa kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AproveAgenda, "Gagal memuat data siswa", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetSiswaByKelas>, t: Throwable) {
                Toast.makeText(this@AproveAgenda, "Gagal memuat data siswa: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}