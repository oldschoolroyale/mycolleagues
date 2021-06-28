package com.brm.mycolleagues.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.snackbar.Snackbar

abstract class NetworkChangeListener: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null){
            if (Common.isConnectedToInternet(context)){
                onNetworkAvailable()
            }
            else{
                onNetworkDisable()
            }
        }

    }
    protected abstract fun onNetworkAvailable()
    protected abstract fun onNetworkDisable()
}