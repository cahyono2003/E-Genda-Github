package com.example.egenda


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class Success : AppCompatActivity() {

//    private lateinit var text: TextView
//    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

//        // Inisialisasi properti lottieAnimationView
//        lottieAnimationView = findViewById(R.id.animation)
//
//        // Terapkan animasi setelah inisialisasi properti
//        lottieAnimationView.animate().translationX(2000F).setDuration(2000).setStartDelay(2900)

        // Tunda untuk navigasi ke halaman selanjutnya
        Handler().postDelayed({
            val intent = Intent(applicationContext, MembuatAgenda::class.java)
            startActivity(intent)
        }, 3000)
    }
}

