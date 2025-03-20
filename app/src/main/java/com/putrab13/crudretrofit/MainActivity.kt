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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.putrab13.crudretrofit.model.Buku
import com.putrab13.crudretrofit.model.Penerbit
import com.putrab13.crudretrofit.model.Pengarang
import com.putrab13.crudretrofit.model.ResponseMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    // Variabel RecyclerView dan Adapter untuk menampilkan daftar buku
    private lateinit var recyclerViewBuku: RecyclerView
    private lateinit var bukuAdapter: BukuAdapter
    private var bukuList = mutableListOf<Buku>() // List untuk menyimpan data buku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Memanggil fungsi onCreate() dari kelas induk (AppCompatActivity)
        setContentView(R.layout.activity_main) // Menetapkan layout untuk aktivitas ini dari file activity_main.xml

        // ============================
        // Inisialisasi RecyclerView
        // ============================
        recyclerViewBuku = findViewById(R.id.recyclerView) // Menghubungkan RecyclerView dengan ID di XML
        recyclerViewBuku.layoutManager = LinearLayoutManager(this) // Menentukan tampilan daftar menggunakan LinearLayoutManager (daftar vertikal)

        // ============================
        // Inisialisasi Adapter RecyclerView
        // ============================
        bukuAdapter = BukuAdapter(
            this, // Konteks aplikasi
            bukuList.toMutableList(), // Data daftar buku yang akan ditampilkan dalam RecyclerView
            { buku -> editBuku(buku) }, // Fungsi yang dipanggil ketika pengguna mengklik tombol edit
            { buku -> deleteBuku(buku) } // Fungsi yang dipanggil ketika pengguna melakukan long click untuk menghapus
        )

        recyclerViewBuku.adapter = bukuAdapter // Menghubungkan RecyclerView dengan Adapter

        // ============================
        // Floating Action Button untuk Menambah Buku
        // ============================
        val fabTambahBuku = findViewById<FloatingActionButton>(R.id.fabTambahBuku) // Menghubungkan FAB dengan ID di XML
        fabTambahBuku.setOnClickListener {
            showTambahBukuDialog() // Menampilkan dialog tambah buku ketika FAB diklik
        }

        // ============================
        // Mengambil Data Buku dari Server
        // ============================
        fetchBuku() // Memanggil fungsi untuk mengambil daftar buku dari server dan menampilkannya di RecyclerView
    }

    /**
     * Fungsi untuk menampilkan dialog edit buku
     */
    private fun editBuku(buku: Buku) {
        // Menggunakan LayoutInflater untuk menghubungkan tampilan XML dengan objek dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_buku, null)

        // Menghubungkan input field dari dialog dengan ID yang ada di XML
        val edtJudul = dialogView.findViewById<EditText>(R.id.edtJudul)
        val spinnerPengarang = dialogView.findViewById<Spinner>(R.id.spinnerPengarang)
        val spinnerPenerbit = dialogView.findViewById<Spinner>(R.id.spinnerPenerbit)
        val edtTahunTerbit = dialogView.findViewById<EditText>(R.id.edtTahunTerbit)
        val edtJumlah = dialogView.findViewById<EditText>(R.id.edtJumlah)

        // ===============================
        // Mengisi field dengan data buku saat ini
        // ===============================
        edtJudul.setText(buku.judul)
        edtTahunTerbit.setText(buku.tahunTerbit.toString())
        edtJumlah.setText(buku.jumlah.toString())

        // Membuat dialog edit buku
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView) // Menampilkan tampilan dialog_edit_buku.xml
            .setTitle("Edit Buku") // Menentukan judul dialog
            .setPositiveButton("Simpan") { _, _ -> // Tombol Simpan
                try {
                    // Mengambil data yang telah diedit oleh pengguna
                    val judul = edtJudul.text.toString()
                    val tahunTerbit = edtTahunTerbit.text.toString().toIntOrNull() ?: 0
                    val jumlah = edtJumlah.text.toString().toIntOrNull() ?: 1

                    // ===============================
                    // Mengambil data pengarang dan penerbit dari Spinner
                    // ===============================
                    val pengarangList = spinnerPengarang.tag as? List<Pengarang>
                    val penerbitList = spinnerPenerbit.tag as? List<Penerbit>

                    // Jika daftar pengarang atau penerbit tidak tersedia, tampilkan pesan error
                    if (pengarangList == null || penerbitList == null) {
                        Toast.makeText(this@MainActivity, "Gagal memuat daftar pengarang/penerbit!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Mengambil ID pengarang dan penerbit yang dipilih pengguna dari Spinner
                    val pengarangId = pengarangList[spinnerPengarang.selectedItemPosition].id
                    val penerbitId = penerbitList[spinnerPenerbit.selectedItemPosition].id

                    // Menampilkan log untuk debugging
                    Log.d("DEBUG", "Updating book ID: ${buku.id}, PengarangID: $pengarangId, PenerbitID: $penerbitId")

                    // Memanggil fungsi update buku ke server
                    updateBuku(buku.id, judul, pengarangId, penerbitId, tahunTerbit, jumlah)

                } catch (e: Exception) {
                    // Jika terjadi error, tampilkan pesan log dan toast
                    Log.e("ERROR", "Terjadi kesalahan saat memperbarui buku", e)
                    Toast.makeText(this@MainActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null) // Tombol Batal
            .create()

        // ===============================
        // Memuat data pengarang dan penerbit ke dalam Spinner
        // ===============================
        loadPengarang(spinnerPengarang, buku.pengarangId) // Memuat daftar pengarang
        loadPenerbit(spinnerPenerbit, buku.penerbitId) // Memuat daftar penerbit

        // Menampilkan dialog ke layar
        dialog.show()
    }


    /**
     * Fungsi untuk memuat daftar pengarang dari server ke dalam Spinner
     *
     * @param spinner Spinner yang akan diisi dengan daftar pengarang
     * @param selectedPengarangId ID pengarang yang saat ini dipilih (digunakan saat edit data)
     */
    private fun loadPengarang(spinner: Spinner, selectedPengarangId: Int) {
        // Memanggil API Retrofit untuk mendapatkan daftar pengarang dari server
        RetrofitClient.instance.getPengarang().enqueue(object : Callback<List<Pengarang>> {

            override fun onResponse(call: Call<List<Pengarang>>, response: Response<List<Pengarang>>) {
                if (response.isSuccessful) {
                    // Mengambil daftar pengarang dari respons API, jika kosong maka buat list kosong
                    val pengarangList = response.body() ?: emptyList()

                    // ============================
                    // Jika daftar pengarang kosong, tampilkan pesan kesalahan
                    // ============================
                    if (pengarangList.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Tidak ada data pengarang!", Toast.LENGTH_SHORT).show()
                        return // Menghentikan proses jika tidak ada data
                    }

                    // ============================
                    // Menyiapkan adapter untuk Spinner
                    // ============================
                    val adapter = ArrayAdapter(
                        this@MainActivity, // Konteks
                        android.R.layout.simple_spinner_dropdown_item, // Layout default untuk Spinner
                        pengarangList.map { it.nama } // Mengambil hanya nama pengarang untuk ditampilkan
                    )
                    spinner.adapter = adapter // Menghubungkan adapter dengan Spinner

                    // ============================
                    // Menyimpan daftar pengarang dalam tag Spinner
                    // ============================
                    spinner.tag = pengarangList

                    // ============================
                    // Mencari posisi pengarang yang sedang diedit
                    // ============================
                    val selectedPosition = pengarangList.indexOfFirst { it.id == selectedPengarangId }

                    // Jika pengarang ditemukan dalam daftar, pilih item yang sesuai
                    if (selectedPosition >= 0) spinner.setSelection(selectedPosition)
                }
            }

            override fun onFailure(call: Call<List<Pengarang>>, t: Throwable) {
                // Jika terjadi kesalahan saat memuat data pengarang, tampilkan pesan error di log
                Log.e("ERROR", "Gagal memuat data pengarang", t)
            }
        })
    }

    /**
     * Fungsi untuk memuat daftar penerbit dari server ke dalam Spinner.
     *
     * Fungsi ini akan melakukan request ke API menggunakan Retrofit untuk mendapatkan daftar penerbit.
     * Jika data berhasil diambil, maka daftar penerbit akan dimasukkan ke dalam Spinner.
     * Jika sedang dalam mode edit, penerbit yang sesuai akan dipilih secara otomatis.
     *
     * @param spinner Spinner yang akan diisi dengan daftar penerbit.
     * @param selectedPenerbitId ID penerbit yang saat ini dipilih (hanya digunakan dalam mode edit).
     */
    private fun loadPenerbit(spinner: Spinner, selectedPenerbitId: Int) {
        // Memanggil API Retrofit untuk mendapatkan daftar penerbit dari server
        RetrofitClient.instance.getPenerbit().enqueue(object : Callback<List<Penerbit>> {

            override fun onResponse(call: Call<List<Penerbit>>, response: Response<List<Penerbit>>) {
                // Mengecek apakah respons dari server berhasil (kode status 200 OK)
                if (response.isSuccessful) {

                    // Mengambil daftar penerbit dari respons API, jika null maka buat list kosong
                    val penerbitList = response.body() ?: emptyList()

                    // ============================
                    // Jika daftar penerbit kosong, tampilkan pesan kesalahan
                    // ============================
                    if (penerbitList.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Tidak ada data penerbit!", Toast.LENGTH_SHORT).show()
                        return // Menghentikan eksekusi fungsi jika tidak ada data penerbit
                    }

                    // ============================
                    // Menyiapkan adapter untuk Spinner
                    // ============================
                    val adapter = ArrayAdapter(
                        this@MainActivity, // Konteks aplikasi
                        android.R.layout.simple_spinner_dropdown_item, // Layout bawaan untuk Spinner
                        penerbitList.map { it.nama } // Menggunakan daftar nama penerbit sebagai item Spinner
                    )

                    // Menghubungkan adapter dengan Spinner
                    spinner.adapter = adapter

                    // ============================
                    // Menyimpan daftar penerbit dalam tag Spinner
                    // ============================
                    spinner.tag = penerbitList // Menyimpan daftar penerbit dalam `tag` Spinner untuk digunakan saat menyimpan data

                    // ============================
                    // Mencari posisi penerbit yang sedang diedit
                    // ============================
                    val selectedPosition = penerbitList.indexOfFirst { it.id == selectedPenerbitId }

                    // Jika penerbit ditemukan dalam daftar, pilih item yang sesuai
                    if (selectedPosition >= 0) spinner.setSelection(selectedPosition)
                }
            }

            override fun onFailure(call: Call<List<Penerbit>>, t: Throwable) {
                // Jika terjadi kesalahan saat memuat data penerbit, tampilkan pesan error di log
                Log.e("ERROR", "Gagal memuat data penerbit", t)
            }
        })
    }

    /**
     * Fungsi untuk memperbarui data buku di server.
     *
     * Fungsi ini akan melakukan validasi data terlebih dahulu sebelum mengirimkan request update ke API.
     * Jika data valid, maka akan dikirim ke server menggunakan Retrofit.
     * Jika update berhasil, daftar buku akan diperbarui dan pengguna akan mendapatkan notifikasi.
     *
     * @param id ID buku yang akan diperbarui.
     * @param judul Judul buku yang diperbarui.
     * @param pengarangId ID pengarang yang dipilih.
     * @param penerbitId ID penerbit yang dipilih.
     * @param tahunTerbit Tahun terbit buku yang diperbarui.
     * @param jumlah Jumlah stok buku yang diperbarui.
     */
    private fun updateBuku(id: Int, judul: String, pengarangId: Int, penerbitId: Int, tahunTerbit: Int, jumlah: Int) {

        // ============================
        // 1️⃣ Validasi Data Input
        // ============================
        if (judul.isEmpty() || pengarangId <= 0 || penerbitId <= 0 || tahunTerbit <= 0 || jumlah <= 0) {
            // Menampilkan pesan kesalahan jika ada input yang kosong atau tidak valid
            Toast.makeText(this, "Data tidak valid! Pastikan semua data diisi dengan benar.", Toast.LENGTH_SHORT).show()
            return // Menghentikan proses update jika data tidak valid
        }

        // ============================
        // 2️⃣ Membuat Objek Buku yang Akan Diperbarui
        // ============================
        val bukuUpdate = Buku(
            id = id,                 // ID buku yang akan diperbarui
            judul = judul,           // Judul buku yang diperbarui
            tahunTerbit = tahunTerbit, // Tahun terbit buku
            jumlah = jumlah,         // Jumlah stok buku
            isbn = "9780000000000",  // ISBN (bisa diubah sesuai kebutuhan)
            pengarangId = pengarangId, // ID pengarang yang dipilih dari Spinner
            penerbitId = penerbitId,   // ID penerbit yang dipilih dari Spinner
            rakKodeRak = "RAK-001"   // Rak buku (bisa diubah sesuai kebutuhan)
        )

        // ============================
        // 3️⃣ Logging Data yang Akan Dikirim
        // ============================
        Log.d(
            "DEBUG",
            "Mengirim data update: ID Buku: $id, Judul: $judul, PengarangID: $pengarangId, PenerbitID: $penerbitId, Tahun: $tahunTerbit, Jumlah: $jumlah"
        )

        // ============================
        // 4️⃣ Mengirim Data ke Server Menggunakan Retrofit
        // ============================
        RetrofitClient.instance.updateBuku(id, bukuUpdate).enqueue(object : Callback<ResponseMessage> {

            // Callback jika respon dari server sukses
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    // ============================
                    // 5️⃣ Berhasil Memperbarui Buku
                    // ============================
                    Log.d("DEBUG", "Update berhasil: ${response.body()?.message}")

                    // Memuat ulang daftar buku agar data terbaru ditampilkan
                    fetchBuku()

                    // Memberikan notifikasi ke pengguna bahwa update berhasil
                    Toast.makeText(applicationContext, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    // ============================
                    // 6️⃣ Gagal Memperbarui Buku
                    // ============================
                    val errorBody = response.errorBody()?.string() // Mengambil pesan error dari response body
                    Log.e("DEBUG", "Gagal update buku: $errorBody")

                    // Memberikan notifikasi kepada pengguna bahwa update gagal
                    Toast.makeText(applicationContext, "Gagal memperbarui buku: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            // Callback jika ada kegagalan koneksi atau error server
            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                // ============================
                // 7️⃣ Kesalahan Koneksi atau Server
                // ============================
                Log.e("DEBUG", "Kesalahan koneksi saat update buku", t)

                // Memberikan notifikasi kepada pengguna bahwa terjadi kesalahan koneksi
                Toast.makeText(applicationContext, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Fungsi untuk menampilkan dialog tambah buku.
     *
     * Dialog ini memungkinkan pengguna untuk memasukkan informasi buku baru,
     * seperti judul, tahun terbit, jumlah, serta memilih pengarang dan penerbit dari Spinner.
     * Setelah data dimasukkan, pengguna dapat menyimpan data ke server dengan mengklik tombol "Simpan".
     */
    private fun showTambahBukuDialog() {
        // ============================
        // 1️⃣ Membuat View Dialog dari Layout
        // ============================
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tambah_buku, null)

        // Menghubungkan elemen UI dari layout dengan variabel dalam kode
        val edtJudul = dialogView.findViewById<EditText>(R.id.edtJudul) // Input untuk judul buku
        val spinnerPengarang = dialogView.findViewById<Spinner>(R.id.spinnerPengarang) // Spinner untuk memilih pengarang
        val spinnerPenerbit = dialogView.findViewById<Spinner>(R.id.spinnerPenerbit) // Spinner untuk memilih penerbit
        val edtTahunTerbit = dialogView.findViewById<EditText>(R.id.edtTahunTerbit) // Input untuk tahun terbit
        val edtJumlah = dialogView.findViewById<EditText>(R.id.edtJumlah) // Input untuk jumlah buku

        // ============================
        // 2️⃣ Membuat Dialog Tambah Buku
        // ============================
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView) // Mengatur tampilan dialog menggunakan layout yang sudah dibuat
            .setTitle("Tambah Buku") // Menetapkan judul dialog
            .setPositiveButton("Simpan") { _, _ -> // Tombol untuk menyimpan buku
                try {
                    // ============================
                    // 3️⃣ Mengambil Data dari Input
                    // ============================
                    val judul = edtJudul.text.toString() // Mengambil judul buku
                    val tahunTerbit = edtTahunTerbit.text.toString().toIntOrNull() ?: 0 // Mengambil tahun terbit
                    val jumlah = edtJumlah.text.toString().toIntOrNull() ?: 1 // Mengambil jumlah buku

                    // ============================
                    // 4️⃣ Mengambil Data Pengarang & Penerbit dari Spinner
                    // ============================
                    val pengarangList = spinnerPengarang.tag as? List<Pengarang> // Mengambil daftar pengarang yang telah dimuat
                    val penerbitList = spinnerPenerbit.tag as? List<Penerbit> // Mengambil daftar penerbit yang telah dimuat

                    // Jika daftar pengarang atau penerbit kosong, tampilkan pesan kesalahan
                    if (pengarangList == null || penerbitList == null || pengarangList.isEmpty() || penerbitList.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Harap pilih pengarang dan penerbit!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton // Menghentikan proses jika data tidak valid
                    }

                    // Mengambil ID pengarang dan ID penerbit yang dipilih di Spinner
                    val pengarangId = pengarangList[spinnerPengarang.selectedItemPosition].id
                    val penerbitId = penerbitList[spinnerPenerbit.selectedItemPosition].id

                    // ============================
                    // 5️⃣ Logging Data yang Akan Dikirim
                    // ============================
                    Log.d("DEBUG", "Pengarang terpilih ID: $pengarangId, Penerbit terpilih ID: $penerbitId")

                    // ============================
                    // 6️⃣ Memanggil Fungsi untuk Menambahkan Buku ke Server
                    // ============================
                    tambahBuku(judul, pengarangId, penerbitId, tahunTerbit, jumlah)
                } catch (e: Exception) {
                    // Jika terjadi error saat mengambil input, tampilkan pesan kesalahan
                    Log.e("ERROR", "Kesalahan saat menambahkan buku", e)
                    Toast.makeText(this@MainActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null) // Tombol untuk membatalkan proses tambah buku
            .create() // Membuat dialog

        // ============================
        // 7️⃣ Memuat Data Pengarang dan Penerbit ke Spinner
        // ============================
        loadPengarang(spinnerPengarang, 0) // Memuat daftar pengarang ke dalam spinner
        loadPenerbit(spinnerPenerbit, 0) // Memuat daftar penerbit ke dalam spinner

        // ============================
        // 8️⃣ Menampilkan Dialog ke Pengguna
        // ============================
        dialog.show()
    }

    /**
     * Fungsi untuk menambahkan buku baru ke dalam database melalui API menggunakan Retrofit.
     *
     * @param judul String - Judul buku yang akan ditambahkan.
     * @param pengarangId Int - ID pengarang yang dipilih dari daftar pengarang.
     * @param penerbitId Int - ID penerbit yang dipilih dari daftar penerbit.
     * @param tahunTerbit Int - Tahun terbit buku yang diinputkan oleh pengguna.
     * @param jumlah Int - Jumlah stok buku yang tersedia.
     */
    private fun tambahBuku(judul: String, pengarangId: Int, penerbitId: Int, tahunTerbit: Int, jumlah: Int) {

        // ===================================================
        // 1️⃣ Membuat Objek Buku Baru
        // ===================================================
        val bukuBaru = Buku(
            id = 0, // ID akan dibuat otomatis oleh server
            judul = judul, // Judul buku yang diinputkan pengguna
            tahunTerbit = tahunTerbit, // Tahun terbit yang diinputkan pengguna
            jumlah = jumlah, // Jumlah stok buku yang diinputkan pengguna
            isbn = "9780000000000", // ISBN default (dapat diubah sesuai kebutuhan)
            pengarangId = pengarangId, // ID pengarang yang dipilih dari spinner
            penerbitId = penerbitId, // ID penerbit yang dipilih dari spinner
            rakKodeRak = "RAK-001" // Kode rak default tempat penyimpanan buku
        )

        // ===================================================
        // 2️⃣ Mengirim Data ke Server Menggunakan Retrofit
        // ===================================================
        RetrofitClient.instance.tambahBuku(bukuBaru).enqueue(object : Callback<ResponseMessage> {

            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                // ===================================================
                // 3️⃣ Jika Respons Berhasil (HTTP 200 OK)
                // ===================================================
                if (response.isSuccessful) {
                    fetchBuku() // Memperbarui daftar buku setelah sukses menambahkan
                    Toast.makeText(applicationContext, "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika respons dari server gagal, tampilkan pesan error
                    Toast.makeText(applicationContext, "Gagal menambahkan buku: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                // ===================================================
                // 4️⃣ Jika Gagal Terhubung ke Server
                // ===================================================
                Toast.makeText(applicationContext, "Gagal menambahkan buku", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /**
     * Fungsi untuk menghapus buku dari database melalui API menggunakan Retrofit.
     *
     * @param buku Buku - Objek buku yang akan dihapus, berisi ID dan informasi buku lainnya.
     */
    private fun deleteBuku(buku: Buku) {

        // ===================================================
        // 1️⃣ Menampilkan Dialog Konfirmasi Hapus Buku
        // ===================================================
        AlertDialog.Builder(this)
            .setTitle("Hapus Buku") // Judul dialog
            .setMessage("Apakah Anda yakin ingin menghapus buku \"${buku.judul}\"?") // Pesan konfirmasi

            // ===================================================
            // 2️⃣ Tombol Hapus (Jika Pengguna Setuju)
            // ===================================================
            .setPositiveButton("Hapus") { _, _ ->

                // ===================================================
                // 3️⃣ Mengirim Permintaan Hapus ke Server
                // ===================================================
                RetrofitClient.instance.deleteBuku(buku.id).enqueue(object : Callback<ResponseMessage> {

                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        // ===================================================
                        // 4️⃣ Jika Respons Berhasil (HTTP 200 OK)
                        // ===================================================
                        if (response.isSuccessful) {
                            Toast.makeText(this@MainActivity, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                            fetchBuku() // Memperbarui daftar buku setelah berhasil dihapus
                        } else {
                            // Jika terjadi error dalam respons server
                            Toast.makeText(this@MainActivity, "Gagal menghapus buku", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        // ===================================================
                        // 5️⃣ Jika Gagal Terhubung ke Server
                        // ===================================================
                        Toast.makeText(this@MainActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            // ===================================================
            // 6️⃣ Tombol Batal (Jika Pengguna Membatalkan)
            // ===================================================
            .setNegativeButton("Batal", null) // Tidak melakukan apa-apa jika dibatalkan
            .show() // Menampilkan dialog konfirmasi
    }


    /**
     * Fungsi untuk mengambil daftar buku dari server menggunakan Retrofit
     * dan menampilkannya dalam RecyclerView.
     */
    private fun fetchBuku() {
        // ===================================================
        // 1️⃣ Memanggil API Retrofit untuk mendapatkan daftar buku
        // ===================================================
        RetrofitClient.instance.getBuku().enqueue(object : Callback<List<Buku>> {

            override fun onResponse(call: Call<List<Buku>>, response: Response<List<Buku>>) {
                // ===================================================
                // 2️⃣ Jika respons sukses (HTTP 200 OK)
                // ===================================================
                if (response.isSuccessful) {
                    // Mengambil daftar buku dari respons API, jika kosong maka buat list kosong
                    val bukuList = response.body() ?: emptyList()

                    // ===================================================
                    // 3️⃣ Menampilkan Data Buku dalam RecyclerView
                    // ===================================================
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity) // Menetapkan layout Linear

                    // Membuat adapter dan menghubungkannya dengan RecyclerView
                    recyclerView.adapter = BukuAdapter(
                        this@MainActivity,
                        bukuList.toMutableList(), // Mengubah daftar menjadi mutable agar dapat dimodifikasi
                        { buku -> editBuku(buku) }, // Menangani klik untuk edit buku
                        { buku -> deleteBuku(buku) } // Menangani long click untuk hapus buku
                    )
                }
            }

            override fun onFailure(call: Call<List<Buku>>, t: Throwable) {
                // ===================================================
                // 4️⃣ Jika terjadi kesalahan saat mengambil data dari server
                // ===================================================
                Toast.makeText(this@MainActivity, "Gagal memuat data buku", Toast.LENGTH_SHORT).show()
            }
        })
    }



}