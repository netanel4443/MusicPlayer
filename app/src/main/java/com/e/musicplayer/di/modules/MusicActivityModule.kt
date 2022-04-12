package com.e.musicplayer.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.androidcleanarchitecture.di.scopes.ActivityScope
import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import com.e.androidcleanarchitecture.di.scopes.ViewModelKey
import com.e.musicplayer.di.viewmodelfactory.ViewModelProviderFactory
import com.e.musicplayer.ui.viewmodels.MusicPlayerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MusicActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(MusicPlayerViewModel::class)
    abstract fun bindMusicPlayerViewModel(viewModel:MusicPlayerViewModel): ViewModel

    @Binds
    @ActivityScope
    abstract fun bindProviderFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory

}