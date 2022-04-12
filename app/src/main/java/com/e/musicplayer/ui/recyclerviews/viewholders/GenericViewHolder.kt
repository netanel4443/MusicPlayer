package com.e.musicplayer.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.GenericItemClickListener



open class GenericViewHolder<T>(view:View):RecyclerView.ViewHolder(view){
    protected open var itemClick:GenericItemClickListener<T>?=null

    open fun setItemClickListener(itmCLick:GenericItemClickListener<T>?){
        itemClick=itmCLick
    }

    open fun bind(item:T){}
}
