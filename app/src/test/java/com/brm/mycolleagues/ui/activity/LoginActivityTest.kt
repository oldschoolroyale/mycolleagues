package com.brm.mycolleagues.ui.activity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginActivityTest{
    @Test
    fun loginCheck(){
        val result = LoginActivity().validator("asdsad ", "1234")
        assertThat(result).isTrue()
    }
}