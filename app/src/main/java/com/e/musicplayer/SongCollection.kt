package com.e.musicplayer

import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class SongCollection @Inject constructor (){

    val songs:ArrayList<SongDetails> =ArrayList()

}