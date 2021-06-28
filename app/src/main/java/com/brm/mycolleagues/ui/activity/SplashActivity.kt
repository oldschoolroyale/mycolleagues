package com.brm.mycolleagues.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.brm.mycolleagues.R
import com.brm.mycolleagues.utils.AppPreferences

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (AppPreferences.username?.isNotEmpty() == true){
                goMain()
            }
            else{
                goLogin()
            }
        }, 1000)
    }

    private fun goMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}