package com.brm.mycolleagues.ui.fragment.list.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonModel(
    val id: Int,
    val name: String,
    val start_time: Long,
    val person_image: String
):Parcelable