package com.brm.mycolleagues.network.api

import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.profile.model.WeekResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @GET("users/allusers")
    fun getAllUsers(): Call<List<PersonModel>>

    @PUT("users/status/{online}")
    fun changeStatus(@Path("online") isOnline: Boolean,
                     @Query("username") username: String,
                    @Query("work_start") workStart: Long): Call<Boolean>

    @Multipart
    @POST("upload/")
    fun uploadImage(@Part file: MultipartBody.Part): Call<String>

    @GET("users/status/month")
    fun getMonth(@Query("username") username: String,
                @Query("time") time: Long):
            Call<Long>

    @GET("users/status/week")
    fun getWeek(@Query("username") username: String):
            Call<WeekResponse>

}