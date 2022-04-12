package com.e.musicplayer.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.e.musicplayer.R
import com.e.musicplayer.ui.services.MusicPlayerService

class MediaPlayerNotification {

    private var remote: RemoteViews? = null
    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    val NEXT_SONG = "nextSong"
    val PREV_SONG = "prevSong"
    val START_MUSIC_ACTIVITY = "startMusicActivity"
    val PLAY_PAUSE_SONG = "playOrPauseSong"

    fun notificate(context: Context) {

        val channelId = context.packageName

        remote = RemoteViews(channelId, R.layout.media_player_notification)


        builder = NotificationCompat.Builder(context, context.packageName).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setCustomContentView(remote)
            setSilent(true)

        }

        builder!!.setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        setIntents(context)

        (context as Service).startForeground(1, builder!!.build())

    }

    private fun setIntents(context: Context) {
        val musicActivityIntent = Intent(
            context,
            MusicPlayerService::class.java
        )
        musicActivityIntent.putExtra(START_MUSIC_ACTIVITY, true)

        val forwardSongIntent = Intent(
            context,
            MusicPlayerService::class.java
        )
        forwardSongIntent.putExtra(NEXT_SONG, true)

        val prevSongBtnIntent = Intent(
            context,
            MusicPlayerService::class.java
        )
        prevSongBtnIntent.putExtra(PREV_SONG, true)

        val playPauseBtnIntent = Intent(
            context,
            MusicPlayerService::class.java
        )
        playPauseBtnIntent.putExtra(PLAY_PAUSE_SONG, true)

        val musicPendingIntent = PendingIntent.getService(
            context,
            1,
            musicActivityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val prevPendingIntent = PendingIntent.getService(
            context,
            2,
            prevSongBtnIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val playPausePendingIntent = PendingIntent.getService(
            context,
            3,
            playPauseBtnIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val forwardPendingIntent = PendingIntent.getService(
            context,
            4,
            forwardSongIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        remote!!.setOnClickPendingIntent(R.id.notification_next_song_btn, forwardPendingIntent)
        remote!!.setOnClickPendingIntent(R.id.notification_prev_song_btn, prevPendingIntent)
        remote!!.setOnClickPendingIntent(
            R.id.notification_play_or_pause_btn_not,
            playPausePendingIntent
        )
        remote!!.setOnClickPendingIntent(R.id.music_player_notification_parent, musicPendingIntent)

    }

    fun updateNotification(songName: String?, context: Context) {
        remote!!.setTextViewText(R.id.notification_song_name, songName)
        NotificationManagerCompat.from(context).notify(1, builder!!.build())
    }

    fun changePlayBtnIcon(icon: Int, context: Context) {

        remote!!.setImageViewResource(R.id.notification_play_or_pause_btn_not, icon)
        NotificationManagerCompat.from(context).notify(1, builder!!.build())

    }
}