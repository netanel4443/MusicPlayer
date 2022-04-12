package com.e.musicplayer.ui.recyclerviews.viewholders

import android.view.View
import com.e.musicplayer.SongDetails
import com.e.musicplayer.databinding.SongRecyclerviewCellDesignBinding
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.CreateVh
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.GenericItemClickListener
import com.e.musicplayer.ui.recyclerviews.viewholders.itemclicklisteners.SongItemClickListener

class CreateSongListViewHolder: CreateVh<SongListRecyclerviewItem> {

    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<SongListRecyclerviewItem>?
    ): GenericViewHolder<SongListRecyclerviewItem> {
        val vh= SongListViewHolder(view)
        vh.setItemClickListener(itemClick)
        return vh
    }

   private inner class SongListViewHolder(view: View)
        :GenericViewHolder<SongListRecyclerviewItem>(view) {

    private var slri:SongListRecyclerviewItem?=null
    private var _itemClick:SongItemClickListener?=null
    private val binding:SongRecyclerviewCellDesignBinding

    init {
        binding=SongRecyclerviewCellDesignBinding.bind(view)
            binding.songCell.setOnClickListener{
                _itemClick!!.playOrPauseMusic(slri!!.song,adapterPosition)
            }
        binding.likeBtn.setOnClickListener {
            _itemClick!!.onFavoriteBtnClick(slri!!,adapterPosition)
        }
        }

    override fun setItemClickListener(itmCLick: GenericItemClickListener<SongListRecyclerviewItem>?) {
        _itemClick=itmCLick as SongItemClickListener
    }

    override fun bind(item: SongListRecyclerviewItem) {
        slri=item
        val songDetails=item.song
        binding.songDuration.text= songDetails.songDuration
        binding.songName.text=songDetails.songName
        binding.songDuration.setTextColor(item.textColor)
        binding.songName.setTextColor(item.textColor)
        binding.likeBtn.setBackgroundResource(item.favoriteIcon)

    }
}
}