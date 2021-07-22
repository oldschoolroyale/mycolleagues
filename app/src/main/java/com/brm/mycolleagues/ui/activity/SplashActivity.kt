package com.brm.mycolleagues.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.brm.mycolleagues.R
import com.brm.mycolleagues.service.NetworkChangeListener
import com.brm.mycolleagues.utils.AppPreferences

class SplashActivity : AppCompatActivity() {

    private lateinit var networkChangeListener: NetworkChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val uri: Uri? = intent.data
        if (uri != null){
            val params = uri.pathSegments
            val id = params[params.size - 1]
            goCreate(id)
        }

        val dialog = Dialog(this, R.style.AppTheme_NoActionbar)
        dialog.setContentView(R.layout.dialog_connection_error)
        dialog.setCancelable(false)

        networkChangeListener = object : NetworkChangeListener(){
        override fun onNetworkAvailable() {
            Thread.sleep(2000)
            dialog.dismiss()
            if (AppPreferences.username?.isNotEmpty() == true){
                goMain()
            }
            else{
                goLogin()
            }
        }

        override fun onNetworkDisable() {
            dialog.show()
        }
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
    private fun goCreate(id: String){
        val intent = Intent(this, CreateUserActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
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