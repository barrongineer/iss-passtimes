package com.barron.isspasstimes

import android.app.Application
import com.barron.isspasstimes.activities.MainActivity
import com.barron.isspasstimes.clients.ISSAPIModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by shaunn on 1/3/2018.
 */
class ISSPassTimesApplication : Application() {

    companion object {
        lateinit var graph: Injector
    }

    @Singleton
    @Component(modules = [(ISSAPIModule::class)])
    interface Injector {
        fun inject(activity: MainActivity)
    }

    override fun onCreate() {
        super.onCreate()

        graph = DaggerISSPassTimesApplication_Injector.builder().build()
    }
}