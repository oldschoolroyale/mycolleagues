package com.brm.mycolleagues.ui.activity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationActivityTest {

    @Test
    fun passwordValidator(){
        val result = RegistrationActivity().passwordValidator("shox123 ")
        assertThat(result).isTrue()
    }
}