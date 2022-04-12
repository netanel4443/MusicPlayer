package com.e.musicplayer.ui.recyclerviews.viewholders.helpers

import android.view.View
import com.e.musicplayer.ui.recyclerviews.viewholders.GenericViewHolder

interface CreateVh<T> {
    fun  createViewHolder(view: View, itemClick: GenericItemClickListener<T>?): GenericViewHolder<T>
}