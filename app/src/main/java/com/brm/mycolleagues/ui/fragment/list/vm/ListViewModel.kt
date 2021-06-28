package com.brm.mycolleagues.ui.fragment.list.vm

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.R
import com.brm.mycolleagues.network.repository.AllUsersRepository
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.BaseResponse
import com.brm.mycolleagues.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel @ViewModelInject constructor(private val repository: AllUsersRepository): ViewModel() {
    private val _loading_status = MutableLiveData<BaseModel<List<PersonModel>>>()
    val loading_status : LiveData<BaseModel<List<PersonModel>>> = _loading_status

    private val _work_status = MutableLiveData<BaseModel<Boolean>>()
    val work_status :LiveData<BaseModel<Boolean>> = _work_status

    private val _is_list_empty = MutableLiveData<Boolean>(false)
    val is_list_empty: LiveData<Boolean> = _is_list_empty

    private fun isListEmptyCheck(list: List<PersonModel>){
        _is_list_empty.value = list.isEmpty()
    }

    private fun sendWorkStatus(isOnline: Boolean){
        viewModelScope.launch {
            _work_status.value = BaseModel(Status.LOADING, null)
            val resp = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                repository.changeStatus(isOnline)
            }
            if (resp.status_code == 200){
                _work_status.value = BaseModel(Status.SUCCESS, resp)
                loadList()
            }
            else{
                _work_status.value = BaseModel(Status.ERROR, resp)
            }
        }
    }

    fun loadList(){
        viewModelScope.launch {
            _loading_status.value = BaseModel(Status.LOADING, null)
             val resp =  withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                 repository.getAllUsers()
                }
            if (resp.status_code == 200){
                if (resp.data?.isNotEmpty() == true){
                    val myList = ArrayList<PersonModel>()
                    for (model in resp.data){if (model.is_online){myList.add(model)}}
                    _loading_status.value = BaseModel(Status.SUCCESS, BaseResponse<List<PersonModel>>(
                            200, myList, null))
                    Log.d("oldschool", resp.data[0].is_online.toString())
                    isListEmptyCheck(myList)
                }
            }
            else{
                _is_list_empty.value = false
                _loading_status.value = BaseModel(Status.ERROR, null)
            }

        }
    }

    private fun startWork(){
        if (AppPreferences.start_time != null){
            sendWorkStatus(false)
        }
        else{
            sendWorkStatus(true)
        }
    }

    fun workAlertDialog(context: Context){
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(context.getString(R.string.start_work_question))
            // if the dialog is cancelable
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.start), DialogInterface.OnClickListener { _, _ ->
//                            dialog, id -> LocationHelper(requireContext(), requireActivity()).locationRequest()
                startWork()
            })
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->}
        val alert = dialogBuilder.create()
        alert.show()
    }
}