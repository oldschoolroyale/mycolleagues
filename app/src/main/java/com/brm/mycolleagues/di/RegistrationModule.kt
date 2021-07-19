package com.brm.mycolleagues.di

import com.brm.mycolleagues.di.annotation.AccountRetrofit
import com.brm.mycolleagues.di.annotation.AuthInterceptor
import com.brm.mycolleagues.di.annotation.LoginInterceptor
import com.brm.mycolleagues.network.api.AuthApi
import com.brm.mycolleagues.network.api.UserApi
import com.brm.mycolleagues.ui.App
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class RegistrationModule {


    @Provides
    @Singleton
    fun gson(): Gson {
        return Gson()
    }


    @AuthInterceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            val url = original.url.newBuilder()
                .build()

            val request = requestBuilder
                .url(url)
                .build()
            chain.proceed(request)
        }
    }

    @LoginInterceptor
    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Provides
    @Singleton
    fun provideHttpClient(@AuthInterceptor auth: Interceptor,
                          @LoginInterceptor login: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .retryOnConnectionFailure(false)
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(auth)
            .addInterceptor(login)
            .build()
    }


    @Provides
    @Singleton
    @AccountRetrofit
    fun provideAccountRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(App.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    @Provides
    @Singleton
    fun provideAuthApi(@AccountRetrofit retrofit: Retrofit): AuthApi{
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(@AccountRetrofit retrofit: Retrofit): UserApi{
        return retrofit.create(UserApi::class.java)
    }
    companion object{
        const val CONNECTION_TIMEOUT_SECONDS: Long = 60
    }
}