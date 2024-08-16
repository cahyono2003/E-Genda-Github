package com.example.egenda

import android.os.Parcel
import android.os.Parcelable

data class GetAllAgenda(
    val status: String,
    val data: List<DataAgenda>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(DataAgenda.CREATOR) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeTypedList(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetAllAgenda> {
        override fun createFromParcel(parcel: Parcel): GetAllAgenda {
            return GetAllAgenda(parcel)
        }

        override fun newArray(size: Int): Array<GetAllAgenda?> {
            return arrayOfNulls(size)
        }
    }
}

data class DataAgenda(
    val jadwal_mulai: String,
    val jadwal_selesai: String,
    val nama_guru: String,
    val kelas: String,
    val mata_pelajaran: String,
    val kompetensi_dasar: String,
    val kegiatan_belajar: String,
    val absensi: List<Absensi>,
    val approved_data: List<ApprovedData>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Absensi.CREATOR) ?: listOf(),
        parcel.createTypedArrayList(ApprovedData.CREATOR) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jadwal_mulai)
        parcel.writeString(jadwal_selesai)
        parcel.writeString(nama_guru)
        parcel.writeString(kelas)
        parcel.writeString(mata_pelajaran)
        parcel.writeString(kompetensi_dasar)
        parcel.writeString(kegiatan_belajar)
        parcel.writeTypedList(absensi)
        parcel.writeTypedList(approved_data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataAgenda> {
        override fun createFromParcel(parcel: Parcel): DataAgenda {
            return DataAgenda(parcel)
        }

        override fun newArray(size: Int): Array<DataAgenda?> {
            return arrayOfNulls(size)
        }
    }
}

data class Absensi(
    val id: Int,
    val status: String,
    val waktu_absen: String,
    val keterangan: String,
    val tanggal_agenda: String,
    val siswa_id: Int,
    val created_at: String,
    val updated_at: String,
    val siswa: Siswa // Ubah dari List<Siswa> menjadi Siswa
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Siswa::class.java.classLoader) ?: Siswa(parcel) // Baca objek Siswa dari Parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(status)
        parcel.writeString(waktu_absen)
        parcel.writeString(keterangan)
        parcel.writeString(tanggal_agenda)
        parcel.writeInt(siswa_id)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeParcelable(siswa, flags) // Tulis objek Siswa ke Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Absensi> {
        override fun createFromParcel(parcel: Parcel): Absensi {
            return Absensi(parcel)
        }

        override fun newArray(size: Int): Array<Absensi?> {
            return arrayOfNulls(size)
        }
    }
}

data class ApprovedData(
    val id: Int,
    val tanggal_agenda: String,
    val signature: String,
    val siswa_id: Int,
    val created_at: String,
    val updated_at: String,
    val siswa: Siswa // Ubah dari Siswa ke List<Siswa>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Siswa::class.java.classLoader) ?: Siswa(parcel) // Baca objek Siswa dari Parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(tanggal_agenda)
        parcel.writeString(signature)
        parcel.writeInt(siswa_id)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeParcelable(siswa, flags) // Tulis objek Siswa ke Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApprovedData> {
        override fun createFromParcel(parcel: Parcel): ApprovedData {
            return ApprovedData(parcel)
        }

        override fun newArray(size: Int): Array<ApprovedData?> {
            return arrayOfNulls(size)
        }
    }
}

data class Siswa(
    val id: Int,
    val nis: String,
    val kelas_id: Int,
    val user_id: Int,
    val created_at: String,
    val updated_at: String,
    val user: UserAbsen // Mengubah dari List<UserAbsen> menjadi UserAbsen tunggal
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(UserAbsen::class.java.classLoader) ?: UserAbsen(parcel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nis)
        parcel.writeInt(kelas_id)
        parcel.writeInt(user_id)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Siswa> {
        override fun createFromParcel(parcel: Parcel): Siswa {
            return Siswa(parcel)
        }

        override fun newArray(size: Int): Array<Siswa?> {
            return arrayOfNulls(size)
        }
    }
}

data class UserAbsen(
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(email_verified_at)
        parcel.writeString(phone)
        parcel.writeString(role)
        parcel.writeString(jenis_kelamin)
        parcel.writeString(tanggal_lahir)
        parcel.writeString(tempat_lahir)
        parcel.writeString(address)
        parcel.writeString(photo)
        parcel.writeString(user_status)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserAbsen> {
        override fun createFromParcel(parcel: Parcel): UserAbsen {
            return UserAbsen(parcel)
        }

        override fun newArray(size: Int): Array<UserAbsen?> {
            return arrayOfNulls(size)
        }
    }
}

