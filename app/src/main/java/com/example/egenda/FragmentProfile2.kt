package com.example.egenda

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment


class FragmentProfile2 : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // Set click listener for logout button
        val buttonLogout = view.findViewById<Button>(R.id.btnLogout2)
        buttonLogout.setOnClickListener {
            logout()
        }

        val buttonEmail = view.findViewById<ImageButton>(R.id.btnemail)
        buttonEmail.setOnClickListener{
            val intent = Intent(activity, EmailActivity::class.java)
            startActivity(intent)
        }

        val buttonTelefon = view.findViewById<ImageButton>(R.id.btntelefon)
        buttonTelefon.setOnClickListener{
            val intent = Intent(activity, NomorTeleponActivity::class.java)
            startActivity(intent)
        }

        val namaPenggunaTextView = view.findViewById<TextView>(R.id.namaPenggunaTextView2)
        val namaPengguna = sharedPreferences.getString("name", "")
        namaPenggunaTextView.text = namaPengguna
    }

    private fun logout() {
        // Clear login status from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.remove("role")
        editor.remove("nama")
        editor.remove("access_token")
        editor.apply()

        // Redirect to login activity
        val intent = Intent(requireContext(), login::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}