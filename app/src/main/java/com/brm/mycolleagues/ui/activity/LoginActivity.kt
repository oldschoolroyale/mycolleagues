package com.brm.mycolleagues.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.brm.mycolleagues.R
import com.brm.mycolleagues.ui.activity.vm.RegistrationViewModel
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.JsonConverter
import com.brm.mycolleagues.utils.Status
import com.wang.avi.AVLoadingIndicatorView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<RegistrationViewModel>()
    @Inject lateinit var jsonConverter: JsonConverter

    private lateinit var loader: AVLoadingIndicatorView
    private var username = ""
    private var password =""

    private val loginObserver = Observer<BaseModel<Boolean>>{
        when(it.status){
            Status.LOADING ->{
                loader.visibility = View.VISIBLE
            }
            Status.SUCCESS ->{
                if (it.response?.data!!){
                    loginViewModel.login(username)
                }
                else{
                    loader.visibility = View.INVISIBLE
                    Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_LONG).show()
                }

            }
            Status.ERROR ->{
                loader.visibility = View.INVISIBLE
                showMessage(it.response?.error_text.toString())
            }
        }
    }
    private val signInObserver = Observer<BaseModel<PersonModel>>{
        when(it.status){
            Status.LOADING -> {}
            Status.SUCCESS -> {
                AppPreferences.username = username
                if (it.response?.data!!.is_online){
                    AppPreferences.is_online = true
                }
                jsonConverter.convertResponse(it.response.data)
                goMain()
            }
            Status.ERROR ->{
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loader = findViewById(R.id.activityLoginLoader)
        loginViewModel.check_status.observe(this, loginObserver)
        loginViewModel.login_status.observe(this, signInObserver)

        sign_up.setOnClickListener {
            goRegistration()
        }

        login_btn.setOnClickListener {
            username = login_edt.text.toString()
            password = password_edt.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()){
                loginViewModel.check(username, password)
            }
            else{Toast.makeText(this, "Все поля должны быть заполненны!", Toast.LENGTH_LONG).show()}
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.d("oldschool", message)
    }

    private fun goMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goRegistration(){
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }
}