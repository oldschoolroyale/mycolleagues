package com.brm.mycolleagues.ui

import androidx.multidex.MultiDexApplication
import com.brm.mycolleagues.utils.AppPreferences
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.setUp(this)
    }

    companion object{
        const val BASE_URL = "https://mycolleagues-test.herokuapp.com/"
    }
}