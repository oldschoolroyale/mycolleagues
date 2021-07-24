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
import com.brm.mycolleagues.databinding.ActivityLoginBinding
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

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private var etUsername = ""
    private var etPassword =""

    private val loginObserver = Observer<BaseModel<Boolean>>{
        when(it.status){
            Status.LOADING ->{
               loginViewModel.changeLoaderVisibility(true)
            }
            Status.SUCCESS ->{
                if (it.response?.data!!){
                    loginViewModel.login(etUsername)
                }
                else{
                    showMessage("Пользователь не найден")
                }
                loginViewModel.changeLoaderVisibility(false)
            }
            Status.ERROR ->{
                loginViewModel.changeLoaderVisibility(false)
                showMessage(it.response?.error_text.toString())
            }
        }
    }

    private val signInObserver = Observer<BaseModel<PersonModel>>{ it ->
        when(it.status){
            Status.LOADING -> {
                loginViewModel.changeLoaderVisibility(true)
            }
            Status.SUCCESS -> {
                loginViewModel.changeLoaderVisibility(true)
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
                loginViewModel.changeLoaderVisibility(true)
                showMessage("Ошибка авторизации")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = loginViewModel

        loginViewModel.apply {
            check_status.observe(this@LoginActivity, loginObserver)
            login_status.observe(this@LoginActivity, signInObserver)
        }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}