package com.putrab13.crudretrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.putrab13.crudretrofit.model.Buku
import com.putrab13.crudretrofit.model.Penerbit
import com.putrab13.crudretrofit.model.Pengarang
import com.putrab13.crudretrofit.model.ResponseMessage

/**
 * Interface `ApiService` berisi deklarasi endpoint yang akan digunakan
 * untuk berkomunikasi dengan API menggunakan Retrofit.
 */
interface ApiService {

    /**
     * **Menambahkan Buku**
     * Endpoint: **POST /buku**
     * Deskripsi: Mengirim data buku dalam bentuk JSON untuk disimpan ke database.
     * @param buku Data buku yang akan ditambahkan.
     * @return Response dari server dalam bentuk `ResponseMessage`.
     */
    @POST("buku")
    fun tambahBuku(@Body buku: Buku): Call<ResponseMessage>

    /**
     * **Mengambil Semua Buku**
     * Endpoint: **GET /buku**
     * Deskripsi: Mengambil daftar semua buku dari database.
     * @return List buku yang tersedia dalam bentuk `List<Buku>`.
     */
    @GET("buku")
    fun getBuku(): Call<List<Buku>>

    /**
     * **Mengambil Detail Buku**
     * Endpoint: **GET /buku/{id}**
     * Deskripsi: Mengambil detail dari buku tertentu berdasarkan ID yang diberikan.
     * @param id ID buku yang akan diambil detailnya.
     * @return Detail buku dalam bentuk `Buku`.
     */
    @GET("buku/{id}")
    fun getDetailBuku(@Path("id") id: Int): Call<Buku>

    /**
     * **Memperbarui Data Buku**
     * Endpoint: **PUT /buku/{id}**
     * Deskripsi: Memperbarui data buku tertentu berdasarkan ID.
     * @param id ID buku yang akan diperbarui.
     * @param buku Data buku yang telah diperbarui.
     * @return Response dari server dalam bentuk `ResponseMessage`.
     */
    @PUT("buku/{id}")
    fun updateBuku(@Path("id") id: Int, @Body buku: Buku): Call<ResponseMessage>

    /**
     * **Menghapus Buku**
     * Endpoint: **DELETE /buku/{id}**
     * Deskripsi: Menghapus buku tertentu berdasarkan ID.
     * @param id ID buku yang akan dihapus.
     * @return Response dari server dalam bentuk `ResponseMessage`.
     */
    @DELETE("buku/{id}")
    fun deleteBuku(@Path("id") id: Int): Call<ResponseMessage>

    /**
     * **Mengambil Daftar Pengarang**
     * Endpoint: **GET /pengarang**
     * Deskripsi: Mengambil daftar semua pengarang dari database.
     * @return List pengarang dalam bentuk `List<Pengarang>`.
     */
    @GET("pengarang")
    fun getPengarang(): Call<List<Pengarang>>

    /**
     * **Mengambil Daftar Penerbit**
     * Endpoint: **GET /penerbit**
     * Deskripsi: Mengambil daftar semua penerbit dari database.
     * @return List penerbit dalam bentuk `List<Penerbit>`.
     */
    @GET("penerbit")
    fun getPenerbit(): Call<List<Penerbit>>
}
