package com.e.musicplayer.data.localdb.utils.objects

import io.realm.*
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField


open class RealmObjectWithList(): RealmObject() {

    @PrimaryKey
    var listName:String=""
     var listt =RealmList<RealmObjectWithList>()


}