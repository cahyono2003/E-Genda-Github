package com.example.egenda

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("jadwal-pelajaran")
    fun JadwalPelajaran(
        @Field("user_id") user_id: String,
        @Field("kelas_id") kelas_id: String
    ): Call<JadwalPelajaran>

    @FormUrlEncoded
    @POST("jadwal-agenda")
    fun addAgenda(
        @Field("tanggal_agenda") tanggal_agenda: String,
        @Field("kompetensi_dasar") kompetensi_dasar: String,
        @Field("kegiatan_belajar") kegiatan_belajar: String,
        @Field("guru_id") guru_id: Int,
        @Field("kelas_id") kelas_id: Int,
        @Field("jadwal_pelajaran_id") jadwal_pelajaran_id: Int,
        @Field("mata_pelajaran_id") mata_pelajaran_id: Int,
        @Field("keterangan") keterangan: String
    ): Call<MembuatAgendaResponse>

    @GET("jadwal-agenda-siswa/{siswa_id}")
    fun getAllAgendaListSiswa(
        @Path("siswa_id") siswaId: String
    ): Call<GetAllAgenda>

    @GET("jadwal-agenda-guru/{guru_id}")
    fun getAllAgendaList(
        @Path("guru_id") guruId: String
    ): Call<GetAllAgenda>

    @GET("siswa-list-by-kelas/{kelas_id}")
    fun getSiswa(
        @Path("kelas_id") kelasId: String
    ): Call<GetSiswaByKelas>

    @POST("approved-agenda")
    @FormUrlEncoded
    fun approveAgenda(
        @Field("tanggal_agenda") tanggalAgenda: String,
        @Field("siswa_id") siswa_id: Int,
        @Field("signature") signature: String
    ): Call<ApproveAgendaResponse>

    @POST("siswa-absen")
    @FormUrlEncoded
    fun simpanAbsensi(
        @Field("id") id: Int,
        @Field("status") status: String,
        @Field("waktu_absen") waktu_absen: String,
        @Field("keterangan") keterangan: String,
        @Field("tanggal_agenda") tanggal_agenda: String,
        @Field("siswa_id") siswa_id: String
    ): Call<Absen>

    @POST("forgot-password")
    @FormUrlEncoded
    fun gantiPassword(
        @Field("email") Email: String,
        @Field("password") newPassword: String
    ): Call<LupaPasswordResponse>
}