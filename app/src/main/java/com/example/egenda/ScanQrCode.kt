package com.example.egenda

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class ScanQrCode : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private val schoolLatitude = -6.3342736
    private val schoolLongitude =  108.3434370
    private val allowedRadius = 500 // Radius dalam meter
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var user_id: String // Nama guru yang login
    private lateinit var sharedPreferences: SharedPreferences
    private var isScanning = false // Flag untuk mencegah pemindaian berulang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        user_id = sharedPreferences.getString("user_id", "").toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            // Mulai mendapatkan lokasi aktual perangkat
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin lokasi belum diberikan, minta izin
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                123
            )
            return
        }

        // Mendapatkan lokasi terakhir pengguna
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Jika lokasi berhasil didapatkan, simpan latitude dan longitude-nya
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    // Setelah mendapatkan lokasi, lanjutkan dengan pemindaian QR code
                    startScanning()
                } else {
                    // Jika lokasi terakhir tidak tersedia, mungkin karena belum pernah dilacak sebelumnya
                    // atau layanan lokasi sedang tidak tersedia
                    Toast.makeText(
                        this@ScanQrCode,
                        "Last location not available. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                // Jika terjadi kesalahan dalam mendapatkan lokasi
                Toast.makeText(
                    this@ScanQrCode,
                    "Failed to get location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = false
        codeScanner.isFlashEnabled = false
        codeScanner.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                if (!isScanning) {
                    isScanning = true // Mencegah pemindaian berulang
                    val scannedQRCode = result.text
                    Log.d("QRCodeScanner", "$scannedQRCode")
                    val distance = calculateDistance(
                        schoolLatitude,
                        schoolLongitude,
                        currentLatitude,
                        currentLongitude
                    )
                    if (distance <= allowedRadius) {
                        val kelas_id = extractClassId(scannedQRCode)
                        fetchSchedule(user_id, kelas_id)
                    } else {
                        Toast.makeText(this, "Anda tidak berada dalam jarak yang diizinkan untuk check-in.", Toast.LENGTH_SHORT).show()
                        isScanning = false // Mengizinkan pemindaian berikutnya
                    }
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun extractClassId(scannedQRCode: String): String {
        val parts = scannedQRCode.split(",")
        return parts[0].split("=")[1] // Mengambil ID kelas dari bagian pertama
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

    val userApi = retrofit.create(UserApi::class.java)

    private fun fetchSchedule(user_id: String, kelas_id: String) {
        val hari = getCurrentDay()
        Log.d("FetchSchedule", "Fetching schedule for $user_id, class $kelas_id on $hari") // Debug log

        val call = userApi.JadwalPelajaran(user_id, kelas_id)
        call.enqueue(object : Callback<JadwalPelajaran> {
            override fun onResponse(call: Call<JadwalPelajaran>, response: Response<JadwalPelajaran>) {
                isScanning = false // Mengizinkan pemindaian berikutnya
                if (response.isSuccessful) {
                    val schedule = response.body()
                    if (schedule != null) {
                        Log.d("JadwalPelajaran", "Status: ${schedule.status}")
                        Log.d("JadwalPelajaran", "Jadwal Mulai: ${schedule.data.jadwal_mulai}")
                        Log.d("JadwalPelajaran", "Jadwal Selesai: ${schedule.data.jadwal_selesai}")
                        Log.d("JadwalPelajaran", "Mata Pelajaran: ${schedule.data.mata_pelajaran.mata_pelajaran}")
                        Log.d("JadwalPelajaran", "Kelas: ${schedule.data.mata_pelajaran.kelas}")
                        val intent = Intent(this@ScanQrCode, MembuatAgenda::class.java).apply {
                            putExtra("status", schedule.status)
                            putExtra("jadwal_mulai", schedule.data.jadwal_mulai)
                            putExtra("jadwal_selesai", schedule.data.jadwal_selesai)
                            putExtra("mata_pelajaran", schedule.data.mata_pelajaran.mata_pelajaran)
                            putExtra("kelas", schedule.data.mata_pelajaran.kelas)
                            putExtra("guru_id", schedule.data.guru_id)
                            putExtra("kelas_id", schedule.data.kelas_id)
                            putExtra("mata_pelajaran_id", schedule.data.mata_pelajaran_id)
                            putExtra("jadwal_pelajaran_id", schedule.data.jadwal_pelajaran_id)
                            putExtra("hari", hari)
                        }
                        startActivity(intent)
                    } else {
                        Log.e("JadwalPelajaran", "Response body is null")
                        runOnUiThread {
                            Toast.makeText(this@ScanQrCode, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("JadwalPelajaran", "Response not successful: ${response.message()}")
                    runOnUiThread {
                        Toast.makeText(this@ScanQrCode, "Response not successful: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<JadwalPelajaran>, t: Throwable) {
                isScanning = false // Mengizinkan pemindaian berikutnya
                Log.e("JadwalPelajaran", "API call failed: ${t.message}", t)
                runOnUiThread {
                    Toast.makeText(this@ScanQrCode, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            Calendar.SUNDAY -> "Minggu"
            else -> "Unknown"
        }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371e3 // Radius bumi dalam meter
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lon2 - lon1)

        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}