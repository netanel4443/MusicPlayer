package com.e.musicplayer.data.localdb.objects

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class CachedSongList: RealmObject() {

    @PrimaryKey
    var listName:String=""
    var songList:RealmList<CachedSongRealmObj> = RealmList()


}