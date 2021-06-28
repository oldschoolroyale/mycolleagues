package com.brm.mycolleagues.ui.activity

import android.app.Dialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.brm.mycolleagues.R
import com.brm.mycolleagues.service.NetworkChangeListener
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
@WithFragmentBindings
class MainActivity : AppCompatActivity() {

    private lateinit var networkChangeListener: NetworkChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.mainNavHost))

        val dialog = Dialog(this, R.style.Theme_MyColleagues_NoActionBar)
        dialog.setContentView(R.layout.dialog_custom_view)
        networkChangeListener = object : NetworkChangeListener(){
            override fun onNetworkAvailable() {
                dialog.dismiss()
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



    override fun onSupportNavigateUp(): Boolean {
        val bind= findNavController(R.id.mainNavHost)
        return super.onSupportNavigateUp() || bind.navigateUp()
    }
}