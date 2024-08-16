package com.example.egenda

data class GetSiswaByKelas(
    val status: String,
    val data: KelasData
)

data class KelasData(
    val id: Int,
    val nama_kelas: String,
    val keterangan: String?,
    val created_at: String,
    val updated_at: String,
    val siswa: List<SiswaData>
)

data class SiswaData(
    val id: Int,
    val nis: String,
    val kelas_id: Int,
    val user_id: Int,
    val created_at: String,
    val updated_at: String,
    val user: UserSiswa
)

data class UserSiswa(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val phone: String,
    val role: String,
    val jenis_kelamin: String,
    val tanggal_lahir: String?,
    val tempat_lahir: String?,
    val address: String,
    val photo: String?,
    val user_status: String,
    val created_at: String,
    val updated_at: String
)
