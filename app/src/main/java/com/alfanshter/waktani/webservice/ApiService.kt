package com.alfanshter.waktani.webservice


import com.alfanshter.waktani.model.UsersModel
import com.alfanshter.waktani.utils.InsertResponse
import com.alfanshter.waktani.utils.WrappedResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/users/user")
    fun daftarakun(
        @Field("email") email : String,
        @Field("telepon") telepon : String,
        @Field("password") password : String,
        @Field("nama") nama : String
    ) : Call<UsersModel>


    @GET("api/users/login/{uid}")
    fun getdetailuid(
        @Path("uid") uid : String
    ) : Call<WrappedResponse<UsersModel>>

    @FormUrlEncoded
    @POST("api/users/login")
    fun createpost(
        @Field("nama") nama : String,
        @Field("email") email : String,
        @Field("telepon") telepon : String,
        @Field("foto") foto : String,
        @Field("uid") uid : String,
        @Field("token") token : String
    ): Call<InsertResponse>


    //SAYURAN
    @FormUrlEncoded
    @POST("api/produk/produk")
    fun insertsayur(
        @Field("foto") foto : String,
        @Field("nama_produk") nama_produk : String,
        @Field("deskripsi") deskripsi : String,
        @Field("manfaat") manfaat : String,
        @Field("penyimpanan") penyimpanan : String,
        @Field("nama_supplier") nama_supplier : String,
        @Field("harga") harga : Int,
        @Field("stok_produk") stok_produk : Int,
        @Field("promo_produk") promo_produk : String,
        @Field("satuan_produk") satuan_produk : String,
        @Field("kategori_produk") kategori_produk : String,
        @Field("status_produk") status_produk : String,
        @Field("waktu_preorder") waktu_preorder : String,
        @Field("uid") uid : String
    ) : Call<InsertResponse>



}