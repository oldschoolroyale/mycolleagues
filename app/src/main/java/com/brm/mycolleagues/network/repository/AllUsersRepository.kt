package com.brm.mycolleagues.network.repository

import com.brm.mycolleagues.network.api.UserApi
import com.brm.mycolleagues.ui.fragment.list.model.MonthModel
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.profile.model.WeekResponse
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseResponse
import java.time.Month
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

    fun changeStatus(isOnline: Boolean, workStart: Long): BaseResponse<Boolean>{
        try {
            val resp = api.changeStatus(
                    isOnline = isOnline,
                    username = AppPreferences.username!!,
                    workStart = workStart).execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }

    fun getMonth(username: String): BaseResponse<Long>{
        try {
            val time = System.currentTimeMillis()
            val resp = api.getMonth(username,time).execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }

    fun getWeek(username: String): BaseResponse<WeekResponse>{
        try {
            val resp = api.getWeek(username).execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())
        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }


}