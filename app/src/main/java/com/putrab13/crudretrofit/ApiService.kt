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

interface ApiService {
    @GET("buku")
    fun getBuku(): Call<List<Buku>>

    @PUT("buku/{id}")
    fun updateBuku(@Path("id") id: Int, @Body buku: Buku): Call<ResponseMessage>

    @GET("pengarang")
    fun getPengarang(): Call<List<Pengarang>>

    @GET("penerbit")
    fun getPenerbit(): Call<List<Penerbit>>
}