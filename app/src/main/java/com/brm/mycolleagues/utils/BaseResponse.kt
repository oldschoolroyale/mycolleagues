package com.brm.mycolleagues.utils

data class BaseResponse <T> (val status_code : Int,val data : T? ,val error_text : String? = null)


enum class Status{
    LOADING,SUCCESS,ERROR
}
data class BaseModel <T>(val status : Status, val response : BaseResponse<T>?)