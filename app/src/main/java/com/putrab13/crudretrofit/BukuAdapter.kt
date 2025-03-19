package com.putrab13.crudretrofit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.putrab13.crudretrofit.model.Buku

class BukuAdapter(
    private val list: MutableList<Buku>,
    private val onEditClick: (Buku) -> Unit

) : RecyclerView.Adapter<BukuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtJudul: TextView = view.findViewById(R.id.txtJudul)
        val txtPengarang: TextView = view.findViewById(R.id.txtPengarang)
        val txtPenerbit: TextView = view.findViewById(R.id.txtPenerbit)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buku, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val buku = list[position]
        holder.txtJudul.text = buku.judul
        holder.txtPengarang.text = "Pengarang: ${buku.pengarang ?: "Tidak diketahui"}"
        holder.txtPenerbit.text = "Penerbit: ${buku.penerbit ?: "Tidak diketahui"}"
        holder.btnEdit.setOnClickListener { onEditClick(buku) }
    }

    override fun getItemCount(): Int = list.size

}