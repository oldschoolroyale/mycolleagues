package com.brm.mycolleagues.network.repository

import com.brm.mycolleagues.network.api.AuthApi
import com.brm.mycolleagues.ui.activity.model.LoginRequest
import com.brm.mycolleagues.ui.activity.model.RegistrationRequest
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.BaseResponse
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val api: AuthApi) {
    fun register(registrationRequest: RegistrationRequest): BaseResponse<Boolean>{
        try {

            val response = api.registration(registrationRequest).execute()
            return BaseResponse(response.code(), response.body(), response.errorBody()?.string())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }

    }

    fun login(username: String): BaseResponse<PersonModel>{
        try {
            val response = api.signIn(username).execute()
            return BaseResponse(response.code(), response.body(), response.errorBody()?.string())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }

    fun check(username: String, password: String): BaseResponse<Boolean>{
        try {
            val response = api.check(LoginRequest(username, password)).execute()
            return BaseResponse(response.code(), response.body(), response.errorBody()?.string())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }
}