package com.putrab13.crudretrofit.model

import com.google.gson.annotations.SerializedName

data class Pengarang(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String
)