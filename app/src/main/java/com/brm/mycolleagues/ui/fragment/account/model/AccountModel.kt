package com.brm.mycolleagues.ui.fragment.account.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "account_table")
data class AccountModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val phone_number: Long,
    val age: Int,
    val image: String
)