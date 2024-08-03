package io.junrdev.github.taskee

import android.app.Application
import timber.log.Timber

class TaskeeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}