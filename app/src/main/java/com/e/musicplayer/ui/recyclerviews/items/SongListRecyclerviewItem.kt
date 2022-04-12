package com.e.musicplayer.ui.recyclerviews.items

import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import com.e.musicplayer.R
import com.e.musicplayer.SongDetails

data class SongListRecyclerviewItem(
    var song: SongDetails=SongDetails(),
    var textColor: Int = Color.WHITE,
    var favoriteIcon:Int=R.drawable.favorite_border_24
) {

}