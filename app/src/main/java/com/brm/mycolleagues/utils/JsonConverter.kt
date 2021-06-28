package com.brm.mycolleagues.utils

import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.google.gson.Gson
import javax.inject.Inject

class JsonConverter @Inject constructor(val gson: Gson) {

    fun convertResponse(personModel: PersonModel){
        val json = gson.toJson(personModel)
        AppPreferences.profile = json
    }

    fun reconvertResponse(): PersonModel{
        val modelString = AppPreferences.profile
        return gson.fromJson(modelString,PersonModel::class.java)
    }
}