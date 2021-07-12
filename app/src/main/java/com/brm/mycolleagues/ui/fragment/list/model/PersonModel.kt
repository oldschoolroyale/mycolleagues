package com.brm.mycolleagues.ui.fragment.list.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonModel(
        var id: Int,
        var username: String,
        var name: String,
        var is_online: Boolean,
        var avatar: String,
        var work_start: Long,
        var last_visit: Long
):Parcelable