package com.example.egenda

data class JadwalPelajaran(
    val status: String,
    val data: JadwalPelajaranData,
    val code: String
)

data class JadwalPelajaranData(
    val jadwal_mulai: String,
    val jadwal_selesai: String,
    val mata_pelajaran: MataPelajaran,
    val kelas_id: Int,
    val mata_pelajaran_id: Int,
    val guru_id: Int,
    val jadwal_pelajaran_id: Int
)

data class MataPelajaran(
    val kelas: String,
    val mata_pelajaran: String
)
