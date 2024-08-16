package com.example.egenda

data class LoginResponse(
    val access_token: String,
    val message: String,
    val token_type: String,
    val data: User?
)

data class User(
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
    val updated_at: String,
    val siswa: dataSiswa?,
    val kelas: String?
)

data class dataSiswa(
    val id: Int,
    val nis: String?,
    val kelas_id: Int,
    val user_id: Int,
    val created_at: String?,
    val updated_at: String?
)