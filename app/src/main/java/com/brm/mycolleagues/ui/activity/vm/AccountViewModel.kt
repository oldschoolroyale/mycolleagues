package com.brm.mycolleagues.ui.activity.vm

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.network.repository.MyAccountRepository
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.File

class AccountViewModel @ViewModelInject constructor(val repository: MyAccountRepository): ViewModel() {

    private val _upload_status = MutableLiveData<BaseModel<String>>()
    val upload_status: LiveData<BaseModel<String>> = _upload_status

    fun upload(file: MultipartBody.Part){
        viewModelScope.launch {
            _upload_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.uploadPhoto(file)
            }
            if (resp.status_code == 200){
                _upload_status.value = BaseModel(Status.SUCCESS, resp)
            }
            else{
                _upload_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }
}