package com.e.musicplayer.ui.recyclerviews.viewholders

import android.view.View
import com.e.musicplayer.databinding.SongListsRecyclerviewCellDesignBinding
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.CreateVh
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.GenericItemClickListener

class CreateAllSongListsViewHolder : CreateVh<String> {

    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<String>?
    ): GenericViewHolder<String> {
        val vh= AllSongListsViewHolder(view)
        vh.setItemClickListener(itemClick)
        return vh
    }

    private inner class AllSongListsViewHolder(private val view: View)
    :GenericViewHolder<String>(view) {

    private val binding:SongListsRecyclerviewCellDesignBinding =
        SongListsRecyclerviewCellDesignBinding.bind(view)

    private var item:String?=null

        init {
            binding.listName.setOnClickListener{
                itemClick!!.onItemClick(item!!)
            }
        }

    override fun bind(item: String) {
        this.item=item
        binding.listName.text=item
    }
}
}