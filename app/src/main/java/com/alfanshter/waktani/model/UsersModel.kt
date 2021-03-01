package com.alfanshter.waktani.model

import com.google.gson.annotations.SerializedName

data class UsersModel(
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("telepon") var telepon: String? = null,
    @SerializedName("nama") var nama: String? = null,
    @SerializedName("photoURL") var photoURL: String? = null

)