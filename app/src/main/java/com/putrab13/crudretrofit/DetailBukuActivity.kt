package com.putrab13.crudretrofit

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.putrab13.crudretrofit.model.Buku
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

/**
 * **DetailBukuActivity**
 * - Menampilkan detail buku yang dipilih dari daftar.
 * - Mengambil data dari server menggunakan Retrofit.
 */
class DetailBukuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_buku)

        // Mengambil ID buku dari intent yang dikirim oleh BukuAdapter
        val bukuId = intent.getIntExtra("BUKU_ID", -1)

        // Jika ID tidak valid, tampilkan pesan error dan tutup activity
        if (bukuId == -1) {
            Toast.makeText(this, "Data buku tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish() // Menutup activity jika ID tidak valid
        }

        // Panggil fungsi untuk mengambil detail buku berdasarkan ID
        fetchDetailBuku(bukuId)
    }

    /**
     * **fetchDetailBuku**
     * - Mengambil detail buku dari server berdasarkan ID yang diterima.
     * - Menggunakan Retrofit untuk mengambil data dari API.
     */
    private fun fetchDetailBuku(id: Int) {
        RetrofitClient.instance.getDetailBuku(id).enqueue(object : Callback<Buku> {
            override fun onResponse(call: Call<Buku>, response: Response<Buku>) {
                if (response.isSuccessful) {
                    val buku = response.body()
                    if (buku != null) {
                        // Menampilkan data buku di tampilan
                        findViewById<TextView>(R.id.txtJudul).text = buku.judul
                        findViewById<TextView>(R.id.txtPengarang).text = "Pengarang: ${buku.pengarang}"
                        findViewById<TextView>(R.id.txtPenerbit).text = "Penerbit: ${buku.penerbit}"
                        findViewById<TextView>(R.id.txtTahunTerbit).text = "Tahun Terbit: ${buku.tahunTerbit}"
                        findViewById<TextView>(R.id.txtJumlah).text = "Jumlah: ${buku.jumlah}"
                        findViewById<TextView>(R.id.txtISBN).text = "ISBN: ${buku.isbn}"
                        findViewById<TextView>(R.id.txtLokasiRak).text = "Lokasi Rak: ${buku.lokasiRak}"
                    }
                } else {
                    // Jika gagal mendapatkan data, tampilkan pesan error
                    Toast.makeText(this@DetailBukuActivity, "Gagal memuat detail buku", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Buku>, t: Throwable) {
                // Jika ada kesalahan koneksi, tampilkan pesan error
                Toast.makeText(this@DetailBukuActivity, "Gagal memuat data buku", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
