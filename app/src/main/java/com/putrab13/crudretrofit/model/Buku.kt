package com.putrab13.crudretrofit.model

import com.google.gson.annotations.SerializedName

data class Buku(
    @SerializedName("id") val id: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("tahun_terbit") val tahunTerbit: Int,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("pengarang_id") val pengarangId: Int,
    @SerializedName("penerbit_id") val penerbitId: Int,
    @SerializedName("rak_kode_rak") val rakKodeRak: String,

    // Nilai default `null` agar tidak wajib diisi saat update
    @SerializedName("pengarang") val pengarang: String? = null,
    @SerializedName("penerbit") val penerbit: String? = null,
    @SerializedName("lokasi_rak") val lokasiRak: String? = null,
//    @SerializedName("total_peminjaman") val totalPeminjaman: Int? = null
)

data class ResponseMessage(
    @SerializedName("message") val message: String
)