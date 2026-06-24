package com.quickthought.orio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OrioApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}