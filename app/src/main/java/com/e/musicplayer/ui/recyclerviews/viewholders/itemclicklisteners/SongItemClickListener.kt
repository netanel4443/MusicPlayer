package com.e.musicplayer.ui.recyclerviews.viewholders.itemclicklisteners

import com.e.musicplayer.SongDetails
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.GenericItemClickListener

interface SongItemClickListener:GenericItemClickListener<SongListRecyclerviewItem> {

    fun playOrPauseMusic(item:SongDetails,position:Int)
    fun onFavoriteBtnClick(slri: SongListRecyclerviewItem, index:Int)
    fun onSaveSongToList(songDetails: SongDetails)
    fun deleteSongFromList(slri: SongListRecyclerviewItem)
}