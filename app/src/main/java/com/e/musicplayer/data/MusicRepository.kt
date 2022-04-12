package com.e.musicplayer.data

import android.app.Application
import android.media.MediaPlayer
import com.e.androidcleanarchitecture.di.scopes.ApplicationScope
import com.e.musicplayer.SongDetails
import com.e.musicplayer.SongsPath
import com.e.musicplayer.data.dataholders.SongLists
import com.e.musicplayer.data.localdb.SavedSongsRepo
import com.e.musicplayer.utils.printIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ApplicationScope
class MusicRepository @Inject constructor(
    private val application: Application,
    private val mediaPlayer: MediaPlayer,
    private val context: Application,
    private val savedSongsRepo: SavedSongsRepo,
    private val songLists: SongLists
) {
    private val TAG = this.javaClass.name

    init {
        attachMediaPlayerListeners()
    }

    private var selectedSongPosition: Int = -1
    private var selectedList = ""


    private val _musicListSubject: BehaviorSubject<HashMap<String, HashMap<String, SongDetails>>> =
        BehaviorSubject.createDefault(HashMap())
    val musicListSubject get() = _musicListSubject

    private val _mediaPlayerState: BehaviorSubject<MediaPlayerState> = BehaviorSubject.create()
    val mediaPlayerState get() = _mediaPlayerState


    fun getMusic(): Single<Unit> {
        if (songLists.allSongLists.isNotEmpty()) {
            _musicListSubject.onNext(songLists.allSongLists)
            return Single.fromCallable { }
        }
        return SongsPath().getAudioFiles(application)
            .subscribeOn(Schedulers.io())
            .zipWith(getSavedMusic()) { allSngs, savedMusic ->

                savedMusic[SongListsNames.allSongs] = allSngs
                songLists.allSongLists = savedMusic

                _musicListSubject.onNext(savedMusic)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())

    }

    // we check if liked list exist ,
    // list isn't exist-> create it , and we understand that the song should be saved because if
    // the list wasn't exist, the song is not liked already.
    //list exist-> check if the song is in the list , if it is , delete it , otherwise ,save it.
    fun checkIfSongIsLiked(songDetails: SongDetails): Single<String> {
        if (songLists.allSongLists.containsKey(SongListsNames.likedSongsList)) {
            val likedList = songLists.allSongLists[SongListsNames.likedSongsList]
            if (likedList!!.contains(songDetails.songName)) {
                return deleteSongFromList(likedList[songDetails.songName]!!)
            }
        }
        return savedSongsRepo.saveSongToList(songDetails, SongListsNames.likedSongsList)
            .doOnSuccess {
                val newSongDetails=songDetails.copy(listName = SongListsNames.likedSongsList)

                if (songLists.allSongLists.containsKey(SongListsNames.likedSongsList)) {
                    songLists.allSongLists[SongListsNames.likedSongsList]!![songDetails.songName] = newSongDetails
                } else {
                    songLists.allSongLists[SongListsNames.likedSongsList] =
                        hashMapOf(newSongDetails.songName to newSongDetails)
                    _musicListSubject.onNext(songLists.allSongLists)
                }
            }
    }

    fun deleteSongFromList(songDetails: SongDetails): Single<String> {
        return savedSongsRepo.deleteSong(songDetails.songName, songDetails.listName)
            .doOnSuccess {
                songLists.allSongLists[songDetails.listName]!!
                    .remove(songDetails.songName)
            }
    }


    private fun getSavedMusic(): Single<HashMap<String, HashMap<String, SongDetails>>> {
        return savedSongsRepo.getItems()
    }

    private fun attachMediaPlayerListeners() {

        mediaPlayer.apply {
            setOnPreparedListener {
                _mediaPlayerState.onNext(
                    MediaPlayerState.Started(
                        songLists.allSongLists.get(selectedList)!!.entries
                            .elementAt(selectedSongPosition)
                            .value
                    )
                )
                start()
            }
            setOnErrorListener { mediaPlayer, what, extra ->
                //todo fix this implementation
                _mediaPlayerState.onNext(MediaPlayerState.Error("error"))
                reset()
                true // needs a reset
            }
            setOnCompletionListener {
                _mediaPlayerState.onNext(MediaPlayerState.Complete)
                playNextSong()
            }
        }
    }

    fun playNextSong() {
        if (selectedSongPosition + 1 < songLists.allSongLists[selectedList]!!.size) {
            selectedSongPosition++
            playByPosition(selectedSongPosition, selectedList)
        }
    }

    fun playPrevSong() {
        printIfDbg(TAG, selectedSongPosition.toString())
        if (selectedSongPosition > 0) {
            selectedSongPosition--
            playByPosition(selectedSongPosition, selectedList)
        }
    }

    fun playByPosition(position: Int, listName: String) {
        selectedSongPosition = position
        selectedList = listName

        val uri = songLists.allSongLists.get(listName)!!
            .entries.elementAt(position).value.songUri
        printIfDbg(TAG, uri.toString())
        uri?.let {
            mediaPlayer.apply {
                reset()
                setDataSource(context, uri)
                prepareAsync()
            }
        }

    }

    fun pauseOrResumeMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _mediaPlayerState.onNext(MediaPlayerState.Paused)

        } else {
            mediaPlayer.start()
            _mediaPlayerState.onNext(MediaPlayerState.Resumed)
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer.release()
//        mediaPlayer=null
    }


}