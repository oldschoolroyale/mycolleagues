package com.brm.mycolleagues.network.repository

import android.net.Uri
import com.brm.mycolleagues.network.api.UserApi
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.BaseResponse
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class MyAccountRepository @Inject constructor(val api: UserApi) {
    fun uploadPhoto(file: MultipartBody.Part): BaseResponse<String> {
        try {
            val resp = api.uploadImage(file = file).execute()
            return BaseResponse(resp.code(), resp.body(), resp.errorBody()?.toString())

        }
        catch (e: Exception){
            return BaseResponse(500, null, e.toString())
        }
    }
}