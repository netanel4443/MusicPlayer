package com.e.musicplayer.data.localdb

import android.net.Uri
import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import com.e.musicplayer.SongDetails
import com.e.musicplayer.data.localdb.objects.CachedSongList
import com.e.musicplayer.data.localdb.objects.CachedSongRealmObj
import com.e.musicplayer.data.localdb.utils.*
import com.e.musicplayer.utils.printIfDbg
import io.reactivex.rxjava3.core.Single
import io.realm.Realm
import javax.inject.Inject

@ApplicationScope
class SavedSongsRepo @Inject constructor(private val realm: Realm) {
private val TAG=javaClass.name

    fun saveSongToList(songDetails: SongDetails,listName: String): Single<String> {
        //CachedSongList is the list which we want to save the song to
       return realm.saveItemToList(CachedSongList::class.java, "listName", listName) {rlm,obj->
           //create new realm object for the new song
            val rlmObj = rlm.createObject(CachedSongRealmObj::class.java)
            rlmObj.songName=songDetails.songName
            rlmObj.songPath = songDetails.songUri.toString()
            rlmObj.id=songDetails.id
            //pair of the song and the list which we want to save the song to
            rlmObj to obj.songList
        }
    }

    fun deleteSong(songName: String,listName:String):Single<String>{
        return realm.deleteItemFromList(CachedSongList::class.java,"listName",listName){
            it.songList.where().equalTo("songName",songName).findFirst()!!
        }
    }

    //get all song lists
    fun getItems():Single<HashMap<String,HashMap<String,SongDetails>>> {

        return realm.getLists(CachedSongList::class.java) { rlmResults ->
            val savedSongsMap = HashMap<String,HashMap<String,SongDetails>>()
            rlmResults.forEach {cachedSongLists->
                val hMap = HashMap<String,SongDetails>()

                cachedSongLists.songList.forEachIndexed { index, cachedSongRealmObj ->
                    val songDetails = SongDetails(
                        songName = cachedSongRealmObj.songName,
                        songUri = Uri.parse(cachedSongRealmObj.songPath),
                        id = cachedSongRealmObj.id,
                        listName = cachedSongLists.listName
                    )
                    hMap[cachedSongRealmObj.songName]=songDetails
                }
                savedSongsMap[cachedSongLists.listName]=hMap
            }
            savedSongsMap
        }
    }
}
