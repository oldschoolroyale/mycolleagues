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
    private var etUsername = ""
    private var etPassword =""

    private val loginObserver = Observer<BaseModel<Boolean>>{
        when(it.status){
            Status.LOADING ->{
                loader.visibility = View.VISIBLE
            }
            Status.SUCCESS ->{
                if (it.response?.data!!){
                    loginViewModel.login(etUsername)
                }
                else{
                    loader.visibility = View.INVISIBLE
                    showMessage("Пользователь не найден")
                }

            }
            Status.ERROR ->{
                loader.visibility = View.INVISIBLE
                showMessage(it.response?.error_text.toString())
            }
        }
    }

    private val signInObserver = Observer<BaseModel<PersonModel>>{ it ->
        when(it.status){
            Status.LOADING -> {}
            Status.SUCCESS -> {
                AppPreferences.apply {
                    username = etUsername
                    if (it.response?.data != null){
                        is_online = it.response.data.is_online
                        with(jsonConverter) { convertResponse(personModel = it.response.data) }
                            .also {
                                goMain()
                            }
                    }
                    else{
                        showMessage("Ошибка, сервер отправил пустую модель")
                    }
                }


            }
            Status.ERROR ->{
                showMessage("Ошибка авторизации")
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
            etUsername = login_edt.text.toString().trim()
            etPassword = password_edt.text.toString().trim()

            if (validator(etUsername, etPassword)){
                loginViewModel.check(etUsername, etPassword)
            }
            else{
                showMessage("Все поля должны быть заполненны!")
            }
        }
    }

    fun validator(username: String, password: String): Boolean{
        return (username.isNotEmpty() && password.isNotEmpty())
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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