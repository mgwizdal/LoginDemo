package com.example.logindemo

import com.example.logindemo.model.LoginUseCase
import com.example.logindemo.model.api.FakeLoginService
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
    //If only the api documentation works...
//    return retrofit.create(LoginService::class.java)
    return FakeLoginService()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://bench-api.applover.pl/api/v1/")
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient().newBuilder().addInterceptor(interceptor).build()
}

fun provideStethoInterceptor(): Interceptor = StethoInterceptor()