package com.brm.mycolleagues.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


object AppPreferences {

    private var sharedPreferences: SharedPreferences? = null

    fun setUp(context: Context){
        sharedPreferences =
            context.getSharedPreferences("Martawallet.sharedprefs", Context.MODE_PRIVATE)
    }

    fun removeAll(){
        sharedPreferences?.edit()?.clear()?.apply()
    }



   var worked_hours: Long?
       get() = Key.WORKED_HOURS.getLong()
    set(value) = Key.WORKED_HOURS.setLong(value)

    var start_time: Long?
        get() = Key.START_TIME.getLong()
        set(value) = Key.START_TIME.setLong(value)

    var end_time: Long?
        get() = Key.END_TIME.getLong()
        set(value) = Key.END_TIME.setLong(value)


    private enum class Key{
        WORKED_HOURS, START_TIME, END_TIME;

        fun getBoolean(): Boolean? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getBoolean(name, false) else null
        fun getBooleanDefaultFalse(): Boolean = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getBoolean(name, true) else false
        fun getFloat(): Float? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getFloat(name, 0f) else null
        fun getInt(): Int? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getInt(name, 0) else null
        fun getLong(): Long? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getLong(name, 0) else null
        fun getString(): String? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(name, "") else null
        fun getByteArray(): ByteArray? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(name, "")?.toByteArray(Charsets.UTF_8) else null
        fun setBoolean(value: Boolean?) = value?.let { sharedPreferences!!.edit { putBoolean(name, value) } } ?: remove()
        fun setFloat(value: Float?) = value?.let { sharedPreferences!!.edit { putFloat(name, value) } } ?: remove()
        fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()
        fun setLong(value: Long?) = value?.let { sharedPreferences!!.edit { putLong(name, value) } } ?: remove()
        fun setString(value: String?) = value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: remove()
        fun setByteArray(value: ByteArray?) = value?.let { sharedPreferences!!.edit { putString(name, String(value, Charsets.UTF_8)) } } ?: remove()
        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}