package com.e.musicplayer

import android.net.Uri
import com.e.musicplayer.data.SongListsNames

data class SongDetails(
    var songName:String = "",
    var songDuration:String="",
    var songUri: Uri? = null,
    var listName:String =SongListsNames.allSongs,
    var id:Int=-1
) {
}