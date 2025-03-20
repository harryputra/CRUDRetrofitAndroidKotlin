package com.putrab13.crudretrofit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.putrab13.crudretrofit.model.Buku

/**
 * **BukuAdapter** adalah adapter untuk RecyclerView yang digunakan untuk menampilkan daftar buku.
 * Adapter ini bertanggung jawab untuk menghubungkan data buku dengan tampilan item dalam RecyclerView.
 *
 * @param context Konteks aplikasi yang digunakan untuk mengakses sumber daya.
 * @param list Daftar buku yang akan ditampilkan dalam RecyclerView.
 * @param onEditClick Callback yang akan dipanggil ketika tombol edit diklik.
 * @param onDeleteClick Callback yang akan dipanggil ketika item di long click untuk dihapus.
 */
class BukuAdapter(
    private val context: Context, // Konteks aplikasi
    private val list: MutableList<Buku>, // Data daftar buku
    private val onEditClick: (Buku) -> Unit, // Callback saat tombol edit diklik
    private val onDeleteClick: (Buku) -> Unit // Callback saat item di long click untuk hapus
) : RecyclerView.Adapter<BukuAdapter.ViewHolder>() {

    /**
     * **ViewHolder** adalah kelas yang berfungsi untuk menampung tampilan setiap item buku dalam RecyclerView.
     * ViewHolder ini menyimpan referensi ke elemen tampilan yang akan digunakan untuk menampilkan data buku.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ==========================
        // Elemen UI dalam item_buku.xml
        // ==========================

        val txtJudul: TextView = view.findViewById(R.id.txtJudul)
        // TextView untuk menampilkan judul buku.

        val txtPengarang: TextView = view.findViewById(R.id.txtPengarang)
        // TextView untuk menampilkan nama pengarang.

        val txtPenerbit: TextView = view.findViewById(R.id.txtPenerbit)
        // TextView untuk menampilkan nama penerbit.

        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        // Tombol Edit, yang digunakan untuk mengedit data buku.
    }

    /**
     * **onCreateViewHolder**: Fungsi ini bertanggung jawab untuk membuat tampilan (View)
     * untuk setiap item dalam daftar RecyclerView.
     *
     * Fungsi ini dipanggil oleh RecyclerView saat pertama kali membuat item tampilan.
     *
     * @param parent ViewGroup yang berisi daftar tampilan item RecyclerView.
     * @param viewType Tipe tampilan item (tidak digunakan dalam contoh ini).
     * @return ViewHolder yang berisi tampilan item yang telah dibuat.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ===================================================
        // 1️⃣ Inflate layout `item_buku.xml` ke dalam View
        // ===================================================
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buku, parent, false)

        // ===================================================
        // 2️⃣ Buat ViewHolder menggunakan tampilan yang dibuat
        // ===================================================
        return ViewHolder(view)
    }


    /**
     * Fungsi `onBindViewHolder` digunakan untuk menghubungkan data buku dengan tampilan pada RecyclerView.
     *
     * @param holder ViewHolder yang akan digunakan untuk menampilkan data.
     * @param position Posisi item dalam daftar buku yang sedang ditampilkan.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ===================================================
        // 1️⃣ Ambil data buku berdasarkan posisi dalam daftar
        // ===================================================
        val buku = list[position]

        // ===================================================
        // 2️⃣ Set data ke tampilan dalam ViewHolder
        // ===================================================
        holder.txtJudul.text = buku.judul
        holder.txtPengarang.text = "Pengarang: ${buku.pengarang ?: "Tidak Diketahui"}"
        holder.txtPenerbit.text = "Penerbit: ${buku.penerbit ?: "Tidak Diketahui"}"

        // ===================================================
        // 3️⃣ Klik tombol Edit -> Panggil fungsi `onEditClick`
        // ===================================================
        holder.btnEdit.setOnClickListener { onEditClick(buku) }

        // ===================================================
        // 4️⃣ Klik item buku -> Buka `DetailBukuActivity`
        // ===================================================
        holder.itemView.setOnClickListener {
            // Buat intent untuk membuka DetailBukuActivity
            val intent = Intent(context, DetailBukuActivity::class.java)
            intent.putExtra("BUKU_ID", buku.id) // Kirim ID buku yang dipilih
            context.startActivity(intent) // Jalankan intent
        }

        // ===================================================
        // 5️⃣ Long Click pada item -> Panggil fungsi `onDeleteClick` untuk menghapus
        // ===================================================
        holder.itemView.setOnLongClickListener {
            onDeleteClick(buku) // Panggil fungsi hapus buku
            true // Mengembalikan `true` agar event long click berjalan
        }
    }

    /**
     * **getItemCount**: Mengembalikan jumlah total item dalam daftar buku.
     *
     * Fungsi ini digunakan oleh RecyclerView untuk mengetahui jumlah data yang akan ditampilkan.
     *
     * @return Jumlah item dalam daftar buku.
     */
    override fun getItemCount(): Int = list.size

}
