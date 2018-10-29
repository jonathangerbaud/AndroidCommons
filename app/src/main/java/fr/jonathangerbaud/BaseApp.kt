package fr.jonathangerbaud

import android.app.Application

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        private lateinit var instance: BaseApp

        fun get(): BaseApp {
            return instance
        }
    }
}
