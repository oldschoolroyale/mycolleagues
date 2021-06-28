package com.brm.mycolleagues.ui.fragment.list.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonModel(
        val id: Long,
        val username: String,
        val name: String,
        val is_online: Boolean,
        val avatar: String,
        val work_start: Long,
        val last_visit: Long,
        val month_id: Int,
        val week_id: Int
):Parcelable