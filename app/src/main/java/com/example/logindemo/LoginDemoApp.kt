package com.example.logindemo

import android.app.Application
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LoginDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LoginDemoApp)
            modules(listOf(appModule, networkModule))
        }
        Stetho.initializeWithDefaults(this)
    }
}