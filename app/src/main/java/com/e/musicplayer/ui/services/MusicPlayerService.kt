package com.e.musicplayer.ui.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.e.musicplayer.BaseApplication
import com.e.musicplayer.R
import com.e.musicplayer.data.MediaPlayerState
import com.e.musicplayer.data.MusicRepository
import com.e.musicplayer.ui.MusicActivity
import com.e.musicplayer.ui.notifications.MediaPlayerNotification
import com.e.musicplayer.utils.printErrorIfDbg
import javax.inject.Inject

class MusicPlayerService : Service() {
    @Inject
    lateinit var musicRepo: MusicRepository
    private val notification: MediaPlayerNotification = MediaPlayerNotification()
    private val TAG = javaClass.name

    override fun onCreate() {
        val appComponent = (application as BaseApplication).appComponent.inject(this)
        super.onCreate()
        attachSubjects()
        notification.notificate(this)

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    private fun attachSubjects() {
        musicRepo.mediaPlayerState.subscribe({ mediaPlayerState ->
            when (mediaPlayerState) {

                is MediaPlayerState.Started -> {
                    notification.changePlayBtnIcon(
                        R.drawable.ic_baseline_pause_circle_outline_24,
                        this
                    )
                    notification.updateNotification(mediaPlayerState.songDetails?.songName, this)
                }

                is MediaPlayerState.Resumed -> {
                    notification.changePlayBtnIcon(
                        R.drawable.ic_baseline_pause_circle_outline_24,
                        this
                    )
                }

                is MediaPlayerState.Paused,
                MediaPlayerState.Complete -> {
                    notification.changePlayBtnIcon(R.drawable.ic_play_circle_outline_24, this)
                }
                is MediaPlayerState.Error -> {
                    //todo need to toast? if yes do it with singleLiveEvent
                }

            }
        }, {
            printErrorIfDbg(TAG, it.message)
        })
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleNotificationClicks(intent)

        return START_STICKY
    }

    private fun handleNotificationClicks(intent: Intent?) {
        val startMusicActivityExtras =
            intent?.getBooleanExtra(notification.START_MUSIC_ACTIVITY, false)
        val playOrPauseSong = intent?.getBooleanExtra(notification.PLAY_PAUSE_SONG, false)
        val nextSong = intent?.getBooleanExtra(notification.NEXT_SONG, false)
        val prevSong = intent?.getBooleanExtra(notification.PREV_SONG, false)

        startMusicActivityExtras?.also {
            if (it) {
                val intentt = Intent(this, MusicActivity::class.java)
                intentt.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentt)
            }
        }
        playOrPauseSong?.also {
            if (it) {
                musicRepo.pauseOrResumeMediaPlayer()
            }
        }
        prevSong?.also {
            if (it) {
                musicRepo.playPrevSong()
            }
        }
        nextSong?.also {
            if (it) {
                musicRepo.playNextSong()
            }
        }
    }

}