package com.brm.mycolleagues.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.brm.mycolleagues.data.AccountRoom
import com.brm.mycolleagues.data.repository.AccountRepository
import com.brm.mycolleagues.ui.fragment.account.model.AccountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(application: Application): AndroidViewModel(application) {

    private val favoriteDao = AccountRoom.getDatabase(
        application
    ).getAccountDao()
    private val repository: AccountRepository

    val getAllData: LiveData<List<AccountModel>>

    init {
        repository = AccountRepository(favoriteDao)
        getAllData = repository.getAllData
    }

    fun insertData(accountModel: AccountModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(accountModel)
        }
    }

    fun updateData(accountModel: AccountModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(accountModel)
        }
    }

    fun deleteFavorite(accountModel: AccountModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(accountModel)
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}