package com.example.logindemo

import com.example.logindemo.model.LoginUseCase
import com.example.logindemo.model.api.LoginService
import com.example.logindemo.utils.RxSchedulers
import com.example.logindemo.viewmodel.LoginActivityViewModel
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { RxSchedulers() }
    viewModel { LoginActivityViewModel(get(), get()) }
    single { LoginUseCase(get()) }
}

val networkModule = module {
    single { provideRetrofit(get()) }
    single { provideLoginService(get()) }
    single { provideOkHttpClient(get()) }
    single { provideStethoInterceptor() }
}

fun provideLoginService(retrofit: Retrofit): LoginService {
    return retrofit.create(LoginService::class.java)
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://applover-login.herokuapp.com/")
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient().newBuilder().addNetworkInterceptor(interceptor).build()
}

fun provideStethoInterceptor(): Interceptor = StethoInterceptor()