package com.example.egenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RiwayatAgendaAdapter(private val agendaList: List<GetAllAgenda>) :
    RecyclerView.Adapter<RiwayatAgendaAdapter.AgendaViewHolder>() {

    inner class AgendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    val mataPelajaranTextView: TextView = itemView.findViewById(R.id.matapelajaran)
//    val jammulaiTextView: TextView = itemView.findViewById(R.id.jammulai)
//    val jamselesaiTextView: TextView = itemView.findViewById(R.id.jamselesai)
//    val statusApproveButton: Button = itemView.findViewById(R.id.statusAprove)
//    val btnLihatDetail: Button = itemView.findViewById(R.id.lihatdetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agenda, parent, false)
        return AgendaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val currentItem = agendaList[position]

//    holder.mataPelajaranTextView.text = currentItem.mata_pelajaran // Adjust as per your data structure
//    holder.jammulaiTextView.text = currentItem.jadwal_mulai
//    holder.jamselesaiTextView.text= currentItem.jadwal_selesai

    // Atur warna tombol berdasarkan status
//    if (currentItem.approved_data.isNotEmpty()) {
//        holder.statusApproveButton.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.shape_status)
//    } else {
//        holder.statusApproveButton.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.shape_button_keluar)
//    }

//    holder.btnLihatDetail.setOnClickListener {
//        val intent = Intent(holder.itemView.context, LihatDetailAgendaActivity::class.java)
//        val getAllAgenda = GetAllAgenda("status", agendaList)
//        intent.putExtra("riawayatAgenda", getAllAgenda)
//        holder.itemView.context.startActivity(intent)
//    }
    }

    override fun getItemCount(): Int {
        return agendaList.size
    }
}