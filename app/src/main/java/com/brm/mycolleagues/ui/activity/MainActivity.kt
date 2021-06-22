package com.brm.mycolleagues.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.brm.mycolleagues.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.mainNavHost))
    }

    override fun onSupportNavigateUp(): Boolean {
        val bind= findNavController(R.id.mainNavHost)
        return super.onSupportNavigateUp() || bind.navigateUp()
    }
}