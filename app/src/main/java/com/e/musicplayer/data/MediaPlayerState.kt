package com.e.musicplayer.data

import com.e.musicplayer.SongDetails

sealed class MediaPlayerState  {
    data class Error(val error:String): MediaPlayerState()
    object Complete : MediaPlayerState()
    data class Started(val songDetails: SongDetails?): MediaPlayerState()
    object Resumed: MediaPlayerState()
    object Paused: MediaPlayerState()
}