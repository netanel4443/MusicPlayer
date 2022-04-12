package com.e.musicplayer.di.components

import android.app.Application
import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import com.e.musicplayer.data.MusicRepository
import com.e.musicplayer.data.localdb.SavedSongsRepo
import com.e.musicplayer.di.modules.AppModule
import com.e.musicplayer.di.modules.ApplicationViewModels
import com.e.musicplayer.di.modules.FavoriteSongRealmModule
import com.e.musicplayer.di.modules.subcomponentmodules.ApplicationComponentSubComponentsModule
import com.e.musicplayer.ui.MusicActivity
import com.e.musicplayer.ui.fragments.SongListFragment
import com.e.musicplayer.ui.services.MusicPlayerService
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        ApplicationViewModels::class,
        AppModule::class,
        FavoriteSongRealmModule::class,
        ApplicationComponentSubComponentsModule::class]
)
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    fun musicActivityComponent():MusicActivityComponent.Factory

//    fun inject(musicActivity: MusicActivity)
    fun inject(service: MusicPlayerService)
    fun inject(repository: MusicRepository)
    fun inject(savedSongsRepo: SavedSongsRepo)
//    fun inject(songListFragment: SongListFragment)
}