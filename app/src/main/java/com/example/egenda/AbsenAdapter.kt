package com.example.egenda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.egenda.databinding.ItemAbsenBinding

class AbsenAdapter(private val absenList: List<Absensi>) : RecyclerView.Adapter<AbsenAdapter.AbsenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenViewHolder {
        val binding = ItemAbsenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbsenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbsenViewHolder, position: Int) {
        val absen = absenList[position]

        // Ambil nama siswa dan status dari Absensi
        val name = absen.siswa.user.name
        val status = absen.status

        // Bind data ke view holder
        holder.bind(name, status)
    }

    override fun getItemCount(): Int {
        return absenList.size
    }

    class AbsenViewHolder(private val binding: ItemAbsenBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, status: String) {
            binding.namaSiswa.text = name
            binding.keterangan.text = status
        }
    }
}