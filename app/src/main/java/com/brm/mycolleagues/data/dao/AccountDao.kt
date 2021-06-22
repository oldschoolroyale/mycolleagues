package com.brm.mycolleagues.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.brm.mycolleagues.ui.fragment.account.model.AccountModel

@Dao
interface AccountDao {
    @Query("SELECT * FROM account_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<AccountModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(accountModel: AccountModel)

    @Update
    suspend fun updateData(accountModel: AccountModel)

    @Delete
    suspend fun deleteItem(accountModel: AccountModel)

    @Query("DELETE FROM account_table")
    suspend fun deleteAll()
}