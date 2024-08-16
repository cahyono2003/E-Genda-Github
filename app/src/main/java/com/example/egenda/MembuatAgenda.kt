package com.example.egenda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

class MembuatAgenda : AppCompatActivity() {

    private lateinit var kelasTextView: TextView
    private lateinit var mataPelajaranTextView: TextView
    private lateinit var jamMulaiTextView: TextView
    private lateinit var jamSelesaiTextView: TextView
    private lateinit var tanggalTextView: TextView
    private lateinit var statusKehadiranTextView: TextView
    private lateinit var kompetensiDasarEditText: EditText
    private lateinit var kegiatanPembelajaranEditText: EditText
    private var kelasId: Int = 0
    private var jadwalPelajaranId: Int = 0
    private var mataPelajaranId: Int = 0
    private var guruId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membuat_agenda)

        // Ambil data dari intent
        val kelas = intent.getStringExtra("kelas")
        val mataPelajaran = intent.getStringExtra("mata_pelajaran")
        val jamMulai = intent.getStringExtra("jadwal_mulai")
        val jamSelesai = intent.getStringExtra("jadwal_selesai")
        kelasId = intent.getIntExtra("kelas_id", 0)
        jadwalPelajaranId = intent.getIntExtra("jadwal_pelajaran_id", 0)
        mataPelajaranId = intent.getIntExtra("mata_pelajaran_id", 0)
        guruId = intent.getIntExtra("guru_id", 0)

        // Inisialisasi UI
        kelasTextView = findViewById(R.id.Kelas)
        mataPelajaranTextView = findViewById(R.id.mataPelajaran)
        jamMulaiTextView = findViewById(R.id.jamMulai)
        jamSelesaiTextView = findViewById(R.id.jamSelesai)
        tanggalTextView = findViewById(R.id.tanggal)
        statusKehadiranTextView = findViewById(R.id.statusKehadiran)
        kompetensiDasarEditText = findViewById(R.id.kompetensiDasar)
        kegiatanPembelajaranEditText = findViewById(R.id.kegiatanPembelajaran)

        // Set data ke UI
        kelasTextView.text = kelas
        mataPelajaranTextView.text = mataPelajaran
        jamMulaiTextView.text = jamMulai
        jamSelesaiTextView.text = jamSelesai

        // Format hari dan tanggal, lalu tampilkan di EditText tanggal
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tanggalTextView.setText(formattedDate)

        // Kodingan btnBatal
        val btnbatal: Button = findViewById(R.id.btnBatal)
        btnbatal.setOnClickListener {
            val intent = Intent(this, BottomNav1::class.java)
            startActivity(intent)
        }

        // Fungsi untuk menambahkan agenda
        val buttonSimpan = findViewById<Button>(R.id.button_simpan)
        buttonSimpan.setOnClickListener {
            val agendaItem = createAgendaItem()
            addAgenda(agendaItem)
        }
    }

    fun addAgenda(agendaItem: Map<String, Any>) {

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
        val call = agendaApi.addAgenda(
            agendaItem["tanggal_agenda"] as String,
            agendaItem["kompetensi_dasar"] as String,
            agendaItem["kegiatan_belajar"] as String,
            agendaItem["guru_id"] as Int,
            agendaItem["kelas_id"] as Int,
            agendaItem["jadwal_pelajaran_id"] as Int,
            agendaItem["mata_pelajaran_id"] as Int,
            agendaItem["keterangan"] as String
        )

        call.enqueue(object : Callback<MembuatAgendaResponse> {
            override fun onResponse(call: Call<MembuatAgendaResponse>, response: Response<MembuatAgendaResponse>) {
                if (response.isSuccessful) {
                    val addedAgenda = response.body()
                    Log.d("Agenda", "Agenda added: $addedAgenda")
                    showSuccessDialog(addedAgenda)
                } else {
                    Log.e("Agenda", "Failed to add agenda: ${response.errorBody()}")
                    Toast.makeText(this@MembuatAgenda, "Gagal menambah agenda", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MembuatAgendaResponse>, t: Throwable) {
                Log.e("Agenda", "Error adding agenda", t)
                Toast.makeText(this@MembuatAgenda, "Error menambah agenda", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showSuccessDialog(addedAgenda: MembuatAgendaResponse?) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Berhasil")
        alertDialog.setMessage("${addedAgenda?.message}")
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            navigateToHome()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun navigateToHome() {
        val intent1 = Intent(this, BottomNav1::class.java)
        startActivity(intent1)
        finish()
    }

    private fun createAgendaItem(): Map<String, Any> {
        val tanggal_agenda = tanggalTextView.text.toString()
        val kompetensi_dasar = kompetensiDasarEditText.text.toString()
        val kegiatan_belajar = kegiatanPembelajaranEditText.text.toString()
        val keterangan = "" // Tambahkan jika ada keterangan

        return mapOf(
            "tanggal_agenda" to tanggal_agenda,
            "kompetensi_dasar" to kompetensi_dasar,
            "kegiatan_belajar" to kegiatan_belajar,
            "guru_id" to guruId,
            "kelas_id" to kelasId,
            "jadwal_pelajaran_id" to jadwalPelajaranId,
            "mata_pelajaran_id" to mataPelajaranId,
            "keterangan" to keterangan
        )
    }


}
