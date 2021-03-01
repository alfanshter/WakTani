package com.alfanshter.waktani.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alfanshter.waktani.model.UsersModel
import com.alfanshter.waktani.session.SessionManager
import com.alfanshter.waktani.utils.InsertResponse
import com.alfanshter.waktani.utils.SingleLiveEvent
import com.alfanshter.waktani.utils.WrappedResponse
import com.alfanshter.waktani.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersViewModel : ViewModel(){
    private var state : SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()

/*
    fun daftar(email :String,telepon : String, password: String, nama : String){
        state.value = UserState.IsLoading(true)
        api.daftarakun(email, telepon, password, nama).enqueue(object : Callback<UsersModel> {
            override fun onFailure(call: Call<UsersModel>, t: Throwable) {
                state.value = UserState.Error(t.message)
            }

            override fun onResponse(call: Call<UsersModel>, response: Response<UsersModel>) {
                state.value = UserState.Success("Pendafataran Sukses")
                if (response.isSuccessful){
                    val body = response.body() as UsersModel
                    if (body.status == 200){
                        state.value = UserState.Success(body.uid)
                    }else{
                        state.value = UserState.Failed("gagal daftar email")
                    }
                }else{
                    state.value = UserState.Failed("kendala server")
                }
                state.value = UserState.IsLoading(false)
            }

        })

    }
*/

    fun getuseruid(uid : String){
        state.value = UserState.IsLoading(true)
        api.getdetailuid(uid).enqueue(object : Callback<WrappedResponse<UsersModel>>{
            override fun onFailure(call: Call<WrappedResponse<UsersModel>>, t: Throwable) {
                state.value = UserState.Error(t.message)
            }

            override fun onResponse(
                call: Call<WrappedResponse<UsersModel>>,
                response: Response<WrappedResponse<UsersModel>>
            ) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse<UsersModel>
                    if (body.kode==true){
                        state.value = UserState.CekUid(true)
                    }else if (body.kode==false){
                        state.value = UserState.CekUid(false)
                    }
                }else{
                    state.value = UserState.Failed("tidak dapat akses server")
                    state.value = UserState.IsLoading(false)
                }

                state.value = UserState.IsLoading(false)
            }

        })
    }


    fun newlogin(nama : String, email : String, telepon : String, foto : String, uid : String, token : String){
        state.value = UserState.IsLoading(true)
        api.createpost(nama,email,telepon,foto,uid,token).enqueue(object : Callback<InsertResponse>{
            override fun onFailure(call: Call<InsertResponse>, t: Throwable) {
                state.value = UserState.Error(t.message)
            }

            override fun onResponse(
                call: Call<InsertResponse>,
                response: Response<InsertResponse>
            ) {
                if (response.isSuccessful){
                    val body = response.body() as InsertResponse
                    if (body.status==200){
                        state.value = UserState.Success(true)
                        state.value = UserState.ShowToast(body.message.toString())
                    }else{
                        state.value = UserState.ShowToast(body.message.toString())
                    }
                }else{
                    state.value = UserState.Error("Tidak dapat terhubung server")
                }
            }

        })

    }

    fun getState() = state

}

sealed class UserState{
    data class Error(var err : String?) : UserState()
    data class ShowToast(var message : String) : UserState()
    data class Validate(var name : String? = null, var email : String? = null, var password : String? = null) : UserState()
    data class IsLoading(var state :Boolean = false) : UserState()
    data class Success(var status :Boolean) : UserState()
    data class Failed(var message :String) : UserState()
    data class  CekUid(var cekuid : Boolean) : UserState()
    data class  Token(var cekuid : String) : UserState()
    object Reset : UserState()
}