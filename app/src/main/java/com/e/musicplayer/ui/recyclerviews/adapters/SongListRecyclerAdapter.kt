package com.e.musicplayer.ui.recyclerviews.adapters

import android.graphics.Color
import com.e.musicplayer.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem
import com.e.musicplayer.ui.recyclerviews.viewholders.CreateSongListViewHolder

class SongListRecyclerAdapter(private val layout:Int,
                              private val clazz: Class<CreateSongListViewHolder>)
    :GenericRecyclerviewAdapter<SongListRecyclerviewItem, CreateSongListViewHolder>(layout,clazz) {


    fun newSongAnimation(prevSongId:Int, newSongId:Int) {

        if (itemCount==0) return
        var indexOfNewPos=-1

           items.forEachIndexed { index, slri->
               if (slri.song.id == prevSongId){
                   items[index].textColor= Color.WHITE
                   notifyItemChanged(index)
               }else if (slri.song.id == newSongId){
                    indexOfNewPos=index
               }
           }
        if(indexOfNewPos > -1){
            items[indexOfNewPos].textColor=Color.rgb(3,169,244)
            notifyItemChanged(indexOfNewPos)
        }

    }

}