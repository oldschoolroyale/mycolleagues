package com.brm.mycolleagues.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brm.mycolleagues.data.dao.AccountDao
import com.brm.mycolleagues.ui.fragment.account.model.AccountModel


@Database(entities = [AccountModel::class], version = 1, exportSchema = false)
abstract class AccountRoom : RoomDatabase() {

    abstract fun getAccountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccountRoom? = null

        fun getDatabase(context: Context): AccountRoom {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountRoom::class.java,
                    "favoriteDB"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}