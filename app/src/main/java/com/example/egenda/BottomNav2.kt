package com.example.egenda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.egenda.databinding.ActivityBottomNav2Binding

class BottomNav2 : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNav2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav2)
        binding = ActivityBottomNav2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment2())
        binding.bottomBar2.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_home2 -> replaceFragment(HomeFragment2())
                R.id.bottom_profile2 -> replaceFragment(FragmentProfile2())
                else -> {
                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment,fragment)
        fragmentTransaction.commit()
    }
}