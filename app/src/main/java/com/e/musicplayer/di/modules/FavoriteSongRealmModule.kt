package com.e.musicplayer.di.modules

import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration

@Module
object FavoriteSongRealmModule {

    @Provides
    @ApplicationScope
    fun getRealmInstance(): Realm {
        val realmName = "FAVORITE_SONG"
        val config: RealmConfiguration = RealmConfiguration.Builder().name(realmName).build()
        return Realm.getInstance(config)
    }

}