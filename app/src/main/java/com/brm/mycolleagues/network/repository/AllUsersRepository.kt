package com.brm.mycolleagues.network.repository

import com.brm.mycolleagues.network.api.UserApi
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseResponse
import javax.inject.Inject

class AllUsersRepository @Inject constructor(val api: UserApi) {
    fun getAllUsers(): BaseResponse<List<PersonModel>>{
        try {
            val resp = api.getAllUsers().execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())

        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }

    fun changeStatus(isOnline: Boolean): BaseResponse<Boolean>{
        try {
            val resp = api.changeStatus(
                    isOnline = isOnline,
                    username = AppPreferences.username!!).execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }
}