package com.brm.mycolleagues.network.api

import com.brm.mycolleagues.ui.activity.model.LoginRequest
import com.brm.mycolleagues.ui.activity.model.RegistrationRequest
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("reg/signup")
    fun registration(@Body registrationRequest:
                     RegistrationRequest):
            Call<Boolean>

    @GET("reg/signin")
    fun signIn(@Query("username") username: String):
            Call<PersonModel>

    @POST("reg/check")
    fun check(@Body loginRequest: LoginRequest):
            Call<Boolean>


}