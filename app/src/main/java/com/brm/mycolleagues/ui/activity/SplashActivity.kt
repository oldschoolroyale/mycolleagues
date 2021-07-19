package com.brm.mycolleagues.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brm.mycolleagues.R
import com.brm.mycolleagues.service.NetworkChangeListener
import com.brm.mycolleagues.utils.AppPreferences

class SplashActivity : AppCompatActivity() {

    private lateinit var networkChangeListener: NetworkChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val dialog = Dialog(this, R.style.AppTheme_NoActionbar)
        dialog.setContentView(R.layout.dialog_connection_error)
        dialog.setCancelable(false)
            if (AppPreferences.username?.isNotEmpty() == true){
                networkChangeListener = object : NetworkChangeListener(){
                    override fun onNetworkAvailable() {
                        dialog.dismiss()
                        goMain()
                    }

                    override fun onNetworkDisable() {
                        dialog.show()
                    }

                }
            }
            else{
                goLogin()
            }
    }

    override fun onStart() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, filter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }


    private fun goMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}