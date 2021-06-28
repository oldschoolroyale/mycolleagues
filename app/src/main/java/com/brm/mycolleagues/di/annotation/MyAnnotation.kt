package com.brm.mycolleagues.di.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginInterceptor

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthInterceptor

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class WalletRetrofit
