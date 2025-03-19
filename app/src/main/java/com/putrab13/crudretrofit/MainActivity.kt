package com.putrab13.crudretrofit

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.putrab13.crudretrofit.model.Buku
import com.putrab13.crudretrofit.model.Penerbit
import com.putrab13.crudretrofit.model.Pengarang
import com.putrab13.crudretrofit.model.ResponseMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewBuku: RecyclerView
    private lateinit var bukuAdapter: BukuAdapter
    private var bukuList = mutableListOf<Buku>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewBuku = findViewById(R.id.recyclerView)
        recyclerViewBuku.layoutManager = LinearLayoutManager(this)
        bukuAdapter = BukuAdapter(bukuList, this::editBuku)
        recyclerViewBuku.adapter = bukuAdapter

        fetchBuku()
    }

    private fun fetchBuku() {
        RetrofitClient.instance.getBuku().enqueue(object : Callback<List<Buku>> {
            override fun onResponse(call: Call<List<Buku>>, response: Response<List<Buku>>) {
                response.body()?.let {
                    bukuList.clear()
                    bukuList.addAll(it)
                    bukuAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Buku>>, t: Throwable) {
                Log.e("API_ERROR", "Gagal mengambil data buku", t)
            }
        })
    }


    private fun editBuku(buku: Buku) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_buku, null)

        val edtJudul = dialogView.findViewById<EditText>(R.id.edtJudul)
        val spinnerPengarang = dialogView.findViewById<Spinner>(R.id.spinnerPengarang)
        val spinnerPenerbit = dialogView.findViewById<Spinner>(R.id.spinnerPenerbit)
        val edtTahunTerbit = dialogView.findViewById<EditText>(R.id.edtTahunTerbit)
        val edtJumlah = dialogView.findViewById<EditText>(R.id.edtJumlah)

        // Set nilai awal
        edtJudul.setText(buku.judul)
        edtTahunTerbit.setText(buku.tahunTerbit.toString())
        edtJumlah.setText(buku.jumlah.toString())

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Buku")
            .setPositiveButton("Simpan") { _, _ ->
                try {
                    val judul = edtJudul.text.toString()
                    val tahunTerbit = edtTahunTerbit.text.toString().toIntOrNull() ?: 0
                    val jumlah = edtJumlah.text.toString().toIntOrNull() ?: 1

                    // Ambil daftar Pengarang dari tag Spinner
                    val pengarangList = spinnerPengarang.tag as? List<Pengarang>
                    val penerbitList = spinnerPenerbit.tag as? List<Penerbit>

                    if (pengarangList == null || penerbitList == null) {
                        Toast.makeText(this@MainActivity, "Gagal memuat daftar pengarang/penerbit!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Ambil ID pengarang dan penerbit dari daftar yang disimpan di tag Spinner
                    val pengarangId = pengarangList[spinnerPengarang.selectedItemPosition].id
                    val penerbitId = penerbitList[spinnerPenerbit.selectedItemPosition].id

                    Log.d("DEBUG", "Updating book ID: ${buku.id}, PengarangID: $pengarangId, PenerbitID: $penerbitId")

                    updateBuku(buku.id, judul, pengarangId, penerbitId, tahunTerbit, jumlah)

                } catch (e: Exception) {
                    Log.e("ERROR", "Terjadi kesalahan saat memperbarui buku", e)
                    Toast.makeText(this@MainActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        // Load data pengarang dan penerbit ke dalam Spinner
        loadPengarang(spinnerPengarang, buku.pengarangId)
        loadPenerbit(spinnerPenerbit, buku.penerbitId)

        dialog.show()
    }
    private fun loadPengarang(spinner: Spinner, selectedPengarangId: Int) {
        RetrofitClient.instance.getPengarang().enqueue(object : Callback<List<Pengarang>> {
            override fun onResponse(call: Call<List<Pengarang>>, response: Response<List<Pengarang>>) {
                if (response.isSuccessful) {
                    val pengarangList = response.body() ?: emptyList()

                    // Gunakan Adapter untuk menyimpan objek Pengarang
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, pengarangList.map { it.nama })
                    spinner.adapter = adapter

                    // Simpan daftar pengarang dalam Tag Spinner
                    spinner.tag = pengarangList

                    // Cari posisi pengarang yang sesuai dengan buku yang sedang diedit
                    val selectedPosition = pengarangList.indexOfFirst { it.id == selectedPengarangId }
                    if (selectedPosition >= 0) {
                        spinner.setSelection(selectedPosition)
                    }
                }
            }

            override fun onFailure(call: Call<List<Pengarang>>, t: Throwable) {
                Log.e("ERROR", "Gagal memuat data pengarang", t)
            }
        })
    }

    private fun loadPenerbit(spinner: Spinner, selectedPenerbitId: Int) {
        RetrofitClient.instance.getPenerbit().enqueue(object : Callback<List<Penerbit>> {
            override fun onResponse(call: Call<List<Penerbit>>, response: Response<List<Penerbit>>) {
                if (response.isSuccessful) {
                    val penerbitList = response.body() ?: emptyList()

                    // Gunakan Adapter untuk menyimpan objek Penerbit
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, penerbitList.map { it.nama })
                    spinner.adapter = adapter

                    // Simpan daftar penerbit dalam Tag Spinner
                    spinner.tag = penerbitList

                    // Cari posisi penerbit yang sesuai dengan buku yang sedang diedit
                    val selectedPosition = penerbitList.indexOfFirst { it.id == selectedPenerbitId }
                    if (selectedPosition >= 0) {
                        spinner.setSelection(selectedPosition)
                    }
                }
            }

            override fun onFailure(call: Call<List<Penerbit>>, t: Throwable) {
                Log.e("ERROR", "Gagal memuat data penerbit", t)
            }
        })
    }
    private fun updateBuku(id: Int, judul: String, pengarangId: Int, penerbitId: Int, tahunTerbit: Int, jumlah: Int) {
        if (judul.isEmpty() || pengarangId <= 0 || penerbitId <= 0 || tahunTerbit <= 0 || jumlah <= 0) {
            Toast.makeText(this, "Data tidak valid! Pastikan semua data diisi dengan benar.", Toast.LENGTH_SHORT).show()
            return
        }

        val bukuUpdate = Buku(
            id = id,
            judul = judul,
            tahunTerbit = tahunTerbit,
            jumlah = jumlah,
            isbn = "9780000000000", // Bisa diubah sesuai kebutuhan
            pengarangId = pengarangId,
            penerbitId = penerbitId,
            rakKodeRak = "RAK-001" // Bisa diubah sesuai kebutuhan
        )

        Log.d("DEBUG", "Mengirim data update: ID Buku: $id, Judul: $judul, PengarangID: $pengarangId, PenerbitID: $penerbitId, Tahun: $tahunTerbit, Jumlah: $jumlah")

        RetrofitClient.instance.updateBuku(id, bukuUpdate).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    Log.d("DEBUG", "Update berhasil: ${response.body()?.message}")
                    fetchBuku()
                    Toast.makeText(applicationContext, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DEBUG", "Gagal update buku: $errorBody")
                    Toast.makeText(applicationContext, "Gagal memperbarui buku: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Log.e("DEBUG", "Kesalahan koneksi saat update buku", t)
                Toast.makeText(applicationContext, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }
}