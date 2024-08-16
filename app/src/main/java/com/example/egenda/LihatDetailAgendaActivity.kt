package com.example.egenda

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LihatDetailAgendaActivity : AppCompatActivity() {

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
    private lateinit var recyclerViewAbsen: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_detail_agenda)

        // Inisialisasi semua elemen UI
        textDataAbsen = findViewById(R.id.dataAbsen)
        tanggal = findViewById(R.id.tanggal)
        kelas = findViewById(R.id.Kelas)
        mataPelajaran = findViewById(R.id.mataPelajaran)
        kompetensiDasar = findViewById(R.id.kompetensidasar)
        kegiatanPembelajaran = findViewById(R.id.kegiatanPembelajaran)
        request = findViewById(R.id.Request)
        jadwalMulai = findViewById(R.id.jamMulai)
        jadwalSelesai = findViewById(R.id.jamSelesai)
        statusAprove = findViewById(R.id.statusAprove)
        recyclerViewAbsen = findViewById(R.id.absenItem)

        // Mendapatkan data agenda dari intent
        val agenda = intent.getParcelableExtra<DataAgenda>("allAgenda")

        // Periksa apakah objek agenda tidak null sebelum menggunakannya
        if (agenda != null) {
            // Mengatur tanggal agenda menjadi tanggal hari ini
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)
            tanggal.text = formattedDate

            // Menampilkan detail agenda
            kelas.text = agenda.kelas
            mataPelajaran.text = agenda.mata_pelajaran
            kompetensiDasar.text = agenda.kompetensi_dasar
            kegiatanPembelajaran.text = agenda.kegiatan_belajar
            jadwalMulai.text = agenda.jadwal_mulai
            jadwalSelesai.text = agenda.jadwal_selesai
            request.text = agenda.nama_guru

            // Memeriksa dan menampilkan status approved_data
            if (agenda.approved_data.isNotEmpty()) {
                statusAprove.text = "Approved"
                statusAprove.background = ContextCompat.getDrawable(this, R.drawable.shapeeditext)
            } else {
                statusAprove.text = "Approve"
            }

            if (agenda.absensi.isNotEmpty()) {
                textDataAbsen.visibility = View.VISIBLE
                recyclerViewAbsen.visibility = View.VISIBLE
                recyclerViewAbsen.layoutManager = LinearLayoutManager(this)
                recyclerViewAbsen.adapter = AbsenAdapter(agenda.absensi)
            } else {
                textDataAbsen.visibility = View.GONE
                recyclerViewAbsen.visibility = View.GONE
            }

        } else {
            Toast.makeText(this, "Data agenda tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
