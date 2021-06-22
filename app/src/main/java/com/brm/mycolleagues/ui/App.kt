package com.brm.mycolleagues.ui

import android.app.Application
import com.brm.mycolleagues.utils.AppPreferences

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.setUp(this)
    }
}