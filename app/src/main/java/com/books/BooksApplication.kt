package com.books

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BooksApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}