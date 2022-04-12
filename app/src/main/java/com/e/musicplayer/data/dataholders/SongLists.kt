package com.e.musicplayer.data.dataholders

import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import com.e.musicplayer.SongDetails
import javax.inject.Inject

@ApplicationScope
class SongLists @Inject constructor(){
    var allSongLists: HashMap<String, HashMap<String, SongDetails>> = HashMap()
}