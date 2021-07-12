package com.brm.mycolleagues.ui.fragment.profile.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.network.repository.AllUsersRepository
import com.brm.mycolleagues.ui.fragment.profile.model.WeekResponse
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel @ViewModelInject constructor(val repository: AllUsersRepository): ViewModel() {

    private val _month_status = MutableLiveData<BaseModel<Long>>()
    val month_status : LiveData<BaseModel<Long>> = _month_status

    private val _week_status = MutableLiveData<BaseModel<WeekResponse>>()
    val week_status : LiveData<BaseModel<WeekResponse>> = _week_status

    fun getWeek(username: String){
        viewModelScope.launch {
            _week_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.getWeek(username)
            }
            if (resp.status_code == 200){
                _week_status.value = BaseModel(Status.SUCCESS, resp)
            }
            else{
                _week_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }

     fun getMonth(username: String){
        viewModelScope.launch {
            _month_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.getMonth(username)
            }
            if (resp.status_code == 200){
                _month_status.value = BaseModel(Status.SUCCESS, resp)
            }
            else{
                _month_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }
}