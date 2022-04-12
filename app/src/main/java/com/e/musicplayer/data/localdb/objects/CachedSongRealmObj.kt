package com.e.musicplayer.data.localdb.objects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.jetbrains.annotations.NotNull

open class CachedSongRealmObj: RealmObject() {
    var songName:String=""
    var songPath:String=""
    var id:Int = -1
}