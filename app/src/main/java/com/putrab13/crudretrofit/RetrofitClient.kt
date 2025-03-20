package com.putrab13.crudretrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * **RetrofitClient** digunakan untuk membuat instance Retrofit
 * yang akan digunakan dalam seluruh aplikasi untuk melakukan request ke API.
 */
object RetrofitClient {
    // URL dasar API (Ganti sesuai dengan IP backend)
    // Jika backend berjalan di jaringan lokal, pastikan IP sesuai dengan server

    private const val BASE_URL = "http://172.16.66.122:3000/"

    // Alternatif
    // private const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * **HttpLoggingInterceptor**
     * - Logging untuk melihat request dan response API di Logcat.
     * - Level `BODY` akan mencetak semua data yang dikirim dan diterima.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * **OkHttpClient**
     * - Digunakan untuk menambahkan interceptor (logging).
     * - Logging berguna untuk debugging (mengecek request & response API).
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Tambahkan interceptor
        .build()

    /**
     * **Instance Retrofit**
     * - `lazy` memastikan Retrofit hanya dibuat **sekali** saat pertama kali dipanggil.
     * - Gunakan `.create(ApiService::class.java)` untuk memanggil API yang didefinisikan.
     */
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Gunakan BASE_URL yang sudah ditentukan
            .addConverterFactory(GsonConverterFactory.create()) // Konversi JSON ke Kotlin
            .client(client) // Gunakan OkHttpClient dengan logging
            .build()
            .create(ApiService::class.java) // Buat instance dari ApiService
    }
}
