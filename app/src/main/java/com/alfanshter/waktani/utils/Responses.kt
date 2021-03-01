package com.alfanshter.waktani.utils

import com.google.gson.annotations.SerializedName

data class WrappedResponse<T>(
    @SerializedName("messsage") var message : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("kode") var kode : Boolean? = null,
    @SerializedName("data") var data : T? = null
)


data class InsertResponse(
    @SerializedName("messsage") var message : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("kode") var kode : Boolean? = null

)

data class WrappedListResponse<T>(
    @SerializedName("messsage") var message : String? = null,
    @SerializedName("status") var status : String? = null,
    @SerializedName("data") var data : List<T>? = null
)