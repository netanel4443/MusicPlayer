package com.e.musicplayer

import android.app.Application
import com.e.musicplayer.di.components.ApplicationComponent
import com.e.musicplayer.di.components.DaggerApplicationComponent
import io.realm.Realm

class BaseApplication : Application() {

    val appComponent: ApplicationComponent =
        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}