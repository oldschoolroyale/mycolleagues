package com.brm.mycolleagues.ui.fragment.users.vm

import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.network.repository.AllUsersRepository
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel @ViewModelInject constructor(val repository: AllUsersRepository): ViewModel() {
    private val _loading_status = MutableLiveData<BaseModel<List<PersonModel>>>()
    val loading_status : LiveData<BaseModel<List<PersonModel>>> = _loading_status
    val loaderVisibilityStatus = ObservableBoolean(false)

    fun loaderStatusChange(boolean: Boolean){
        loaderVisibilityStatus.set(boolean)
    }

    fun loadUsers(){
        viewModelScope.launch {
            _loading_status.value = BaseModel(Status.LOADING, null)
            val request =
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    repository.getAllUsers()
                }
            if (request.status_code == 200){
                _loading_status.value = BaseModel(Status.SUCCESS, request)
            }
            else{
                _loading_status.value = BaseModel(Status.ERROR, request)
            }
        }
    }
}