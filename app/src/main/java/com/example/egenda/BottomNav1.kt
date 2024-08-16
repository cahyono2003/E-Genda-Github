package com.example.egenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.egenda.databinding.ActivityBottomNav1Binding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BottomNav1 : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNav1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNav1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment1())
        binding.bottomBar1.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_home1 -> replaceFragment(HomeFragment1())
                R.id.bottom_profile1 -> replaceFragment(ProfileFragment1())
                else -> {
                }
            }
            true
        }
        val btnScan: FloatingActionButton = findViewById(R.id.buttonScanner)
        btnScan.setOnClickListener {
            // Tindakan yang dijalankan saat tombol diklik
            val intent = Intent(this, ScanQrCode::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment,fragment)
        fragmentTransaction.commit()
    }
}