package com.brm.mycolleagues.service

import android.content.Context
import android.net.ConnectivityManager

class Common {
    companion object{
        fun isConnectedToInternet(context: Context): Boolean {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }

}