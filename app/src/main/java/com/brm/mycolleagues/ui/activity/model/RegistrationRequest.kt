package com.brm.mycolleagues.ui.activity.model

import com.brm.mycolleagues.ui.fragment.list.model.MonthModel

data class RegistrationRequest(
        val username: String,
        val password: String,
        val name: String,
        val is_online: Boolean,
        val avatar: String,
        val work_start: Long,
        val last_visit: Long,
        val month: MonthModel
)