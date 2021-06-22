package com.brm.mycolleagues.data.repository

import androidx.lifecycle.LiveData
import com.brm.mycolleagues.data.dao.AccountDao
import com.brm.mycolleagues.ui.fragment.account.model.AccountModel

class AccountRepository(private val accountDao: AccountDao) {
    val getAllData: LiveData<List<AccountModel>> = accountDao.getAllData()

    suspend fun insertData(accountModel: AccountModel){
        accountDao.insertData(accountModel)
    }

    suspend fun updateData(accountModel: AccountModel){
        accountDao.updateData(accountModel)
    }

    suspend fun deleteItem(accountModel: AccountModel){
        accountDao.deleteItem(accountModel)
    }

    suspend fun deleteAll(){
        accountDao.deleteAll()
    }
}