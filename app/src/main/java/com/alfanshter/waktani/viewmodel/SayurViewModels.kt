package com.alfanshter.waktani.viewmodel

import androidx.lifecycle.ViewModel
import com.alfanshter.waktani.utils.InsertResponse
import com.alfanshter.waktani.utils.SingleLiveEvent
import com.alfanshter.waktani.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SayurViewModels : ViewModel() {
    private var state: SingleLiveEvent<SayurState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun insertproduk(
        foto: String,
        nama_produk : String,
        deskripsi : String,
        manfaat : String,
        penyimpanan : String,
        nama_supplier : String,
        harga : Int,
        stok_produk : Int,
        promo_produk : String,
        satuan_produk : String,
        kategori_produk : String,
        status_produk : String,
        waktu_preorder : String,
        uid : String
    ) {
        state.value = SayurState.IsLoading(true)
        api.insertsayur(foto,nama_produk,deskripsi,manfaat,penyimpanan,nama_supplier,harga,stok_produk,promo_produk,
            satuan_produk,kategori_produk,status_produk,waktu_preorder, uid).enqueue(object :Callback<InsertResponse>{
            override fun onFailure(call: Call<InsertResponse>, t: Throwable) {
                state.value = SayurState.Error(t.message)
            }

            override fun onResponse(
                call: Call<InsertResponse>,
                response: Response<InsertResponse>
            ) {
                if (response.isSuccessful){
                    val body = response.body() as InsertResponse
                    if (body.kode==true){
                        state.value = SayurState.Success(true)
                        state.value = SayurState.IsLoading(false)

                    }else{
                        state.value = SayurState.Success(false)
                        state.value = SayurState.IsLoading(false)
                    }

                }else{
                    state.value = SayurState.Failed("Tidak dapat terhubung server")
                    state.value = SayurState.IsLoading(false)
                }
            }

        })

    }


    fun getState() = state

}


sealed class SayurState {
    data class Error(var err: String?) : SayurState()
    data class ShowToast(var message: String) : SayurState()
    data class Validate(
        var name: String? = null,
        var email: String? = null,
        var password: String? = null
    ) : SayurState()

    data class IsLoading(var state: Boolean = false) : SayurState()
    data class Success(var status: Boolean) : SayurState()
    data class Failed(var message: String) : SayurState()
    data class CekUid(var cekuid: Boolean) : SayurState()
    object Reset : SayurState()
}