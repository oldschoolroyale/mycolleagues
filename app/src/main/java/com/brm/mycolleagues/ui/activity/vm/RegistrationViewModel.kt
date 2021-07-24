package com.brm.mycolleagues.ui.activity.vm

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.brm.mycolleagues.network.repository.RegistrationRepository
import com.brm.mycolleagues.ui.activity.model.RegistrationRequest
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.*

class RegistrationViewModel @ViewModelInject constructor(private val repository: RegistrationRepository): ViewModel() {

    private val _registration_status = MutableLiveData<BaseModel<Boolean>>()
    val registration_status : LiveData<BaseModel<Boolean>> = _registration_status

    val loaderVisibilityStatus = ObservableBoolean(false)

    fun changeLoaderVisibility(boolean: Boolean){
        loaderVisibilityStatus.set(boolean)
    }

    fun startRegistration(registrationRequest: RegistrationRequest){
        _registration_status.value = BaseModel(Status.LOADING, null)
        viewModelScope.launch {
            val response = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                 repository.register(registrationRequest)
            }
            Log.d("oldschool", response.status_code.toString() + response.error_text)
            if (response.status_code == 200){
                _registration_status.value = BaseModel(Status.SUCCESS, response)
            }
            else{
                _registration_status.value = BaseModel(Status.ERROR, response)
            }

        }
    }

    private val _login_status = MutableLiveData<BaseModel<PersonModel>>()
    val login_status: LiveData<BaseModel<PersonModel>> = _login_status

    fun login(username: String){
        viewModelScope.launch {
            _login_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.login(username)
            }
            if (resp.status_code == 200){
                _login_status.value = BaseModel(Status.SUCCESS, resp)
            }
            else{
                _login_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }
    private val _check_status = MutableLiveData<BaseModel<Boolean>>()
    val check_status: LiveData<BaseModel<Boolean>> = _check_status

    fun check(username: String, password: String){
        viewModelScope.launch {
            _check_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.check(username, password)
            }
            if (resp.status_code == 200){
                _check_status.value = BaseModel(Status.SUCCESS, resp)
            }
            else{
                _check_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }
}