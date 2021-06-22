package com.brm.mycolleagues.ui.fragment.list.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.BaseResponse
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel: ViewModel() {
    private val _loading_status = MutableLiveData<BaseModel<List<PersonModel>>>()
    val loading_status : LiveData<BaseModel<List<PersonModel>>> = _loading_status

    fun loadList(){
        viewModelScope.launch {
            _loading_status.value = BaseModel(Status.LOADING, null)
            val list = ArrayList<PersonModel>()
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    list.add(PersonModel(0, "Rakhimjonov Shokhsulton", 1624355469,
                        "https://i06.fotocdn.net/s122/90c754bad68cd4db/user_xl/2782181550.jpg"))
                    list.add(PersonModel(1, "Kadirov Bobur", 	1624323069,
                        "https://i.ytimg.com/vi/YwdBoT-ZfdM/maxresdefault.jpg"))
                    list.add(PersonModel(2, "Sotvoldiev Asadbek", 	1624326669,
                        "https://cdn-images-1.medium.com/fit/t/1500/999/1*9alQR86z6DCRqC9pJNlkUw@2x.jpeg"))
                    list.add(PersonModel(3, "Parpiev Azamat", 	1624333869,
                        "https://st4.depositphotos.com/11585370/21030/i/950/depositphotos_210306566-stock-photo-close-shot-handsome-smiling-unshaven.jpg"))
                }
            if (list.isNotEmpty()){
                _loading_status.value = BaseModel(Status.SUCCESS, BaseResponse<List<PersonModel>>(200, list, null))
            }
            else{
                _loading_status.value = BaseModel(Status.ERROR, null)
            }

        }
    }

    fun startWork(){
        AppPreferences.start_time = System.currentTimeMillis()
        Log.d("oldschool", "startWork")
    }

    fun endWork(){
        if (AppPreferences.start_time != null){
            val endTime = System.currentTimeMillis()
            AppPreferences.end_time = endTime
            AppPreferences.worked_hours = endTime - AppPreferences.start_time!!
            AppPreferences.start_time = null
            Log.d("oldschool", AppPreferences.worked_hours.toString())
        }
    }
}