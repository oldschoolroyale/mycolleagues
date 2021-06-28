package com.brm.mycolleagues.network.api

import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @GET("users/allusers")
    fun getAllUsers(): Call<List<PersonModel>>

    @PUT("users/status/{online}")
    fun changeStatus(@Path("online") isOnline: Boolean,
                     @Query("username") username: String): Call<Boolean>

    @Multipart
    @POST("upload/")
    fun uploadImage(@Part file: MultipartBody.Part): Call<String>

}