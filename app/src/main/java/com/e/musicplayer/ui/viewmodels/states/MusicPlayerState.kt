package com.e.musicplayer.ui.viewmodels.states

import com.e.musicplayer.R
import com.e.musicplayer.SongDetails
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem

data class MusicPlayerState(
    var songLists: ArrayList<SongListRecyclerviewItem> = ArrayList(),
    var listsName: HashSet<String> = HashSet(),
    var currentPlayingId: Int = -1,
    var prevPlayedId: Int = -1,
    var playBtnIcon: Int = R.drawable.ic_play_circle_outline_24
)

