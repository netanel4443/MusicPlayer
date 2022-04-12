package com.e.musicplayer.di.components

import com.e.androidcleanarchitecture.di.scopes.ActivityScope
import com.e.musicplayer.di.modules.MusicActivityModule
import com.e.musicplayer.ui.MusicActivity
import com.e.musicplayer.ui.fragments.SongListFragment
import dagger.Component
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [MusicActivityModule::class])
interface MusicActivityComponent {

    @Subcomponent.Factory
    interface Factory{
        fun create():MusicActivityComponent
    }

    fun inject(musicActivity: MusicActivity)
    fun inject(songListFragment: SongListFragment)
}