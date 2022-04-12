package com.e.musicplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import com.e.androidcleanarchitecture.di.scopes.ActivityScope
import com.e.musicplayer.R
import com.e.musicplayer.data.MediaPlayerState
import com.e.musicplayer.data.MusicRepository
import com.e.musicplayer.data.SongListsNames
import com.e.musicplayer.data.dataholders.SongLists
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem
import com.e.musicplayer.ui.utils.MviMutableLiveData
import com.e.musicplayer.ui.utils.PrevAndCurrentState
import com.e.musicplayer.ui.viewmodels.states.MusicPlayerState
import com.e.musicplayer.utils.printErrorIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.currentCoroutineContext
import javax.inject.Inject

@ActivityScope
class MusicPlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val songLists: SongLists
) : BaseViewModel() {
    private val TAG = this.javaClass.name
    private val _viewState = MviMutableLiveData(MusicPlayerState())
    val viewState: LiveData<PrevAndCurrentState<MusicPlayerState>> get() = _viewState


    init {
        attachMusicRepoSubjects()
    }

    private fun attachMusicRepoSubjects() {

        disposables.add(musicRepository.musicListSubject
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                _viewState.mviValue { state ->
                    state.copy(listsName = songLists.allSongLists.keys.toHashSet())
                }
            }
            .subscribe({}, { printErrorIfDbg(TAG, it.message) })
        )

        disposables.add(
            musicRepository.mediaPlayerState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mediaPlayerState ->
                    when (mediaPlayerState) {
                        is MediaPlayerState.Started -> {
                            _viewState.mviValue {
                                it.copy(playBtnIcon = R.drawable.ic_baseline_pause_circle_outline_24)
                            }
                            markSelectedSongUnMarkPrevSong(mediaPlayerState.songDetails!!.id)
                        }
                        is MediaPlayerState.Resumed -> {
                            _viewState.mviValue {
                                it.copy(playBtnIcon = R.drawable.ic_baseline_pause_circle_outline_24)
                            }
                        }
                        is MediaPlayerState.Paused -> {

                            _viewState.mviValue {
                                it.copy(playBtnIcon = R.drawable.ic_play_circle_outline_24)
                            }
                        }
                        is MediaPlayerState.Error -> {
                            //todo need to toast? if yes, do it with singleLiveEvent
                        }
                        is MediaPlayerState.Complete -> {
                            _viewState.mviValue {
                                it.copy(playBtnIcon = R.drawable.ic_play_circle_outline_24)
                            }
                        }
                    }
                }, { printErrorIfDbg(TAG, it.message) })
        )
    }


    fun createItemsForSongListRecyclerView(listName: String) {

        val arrayList = ArrayList<SongListRecyclerviewItem>()
        val songsMap = songLists.allSongLists[listName]
        songsMap!!.forEach { songDetails ->

            val slri = SongListRecyclerviewItem(songDetails.value)
            val favoriteSongs = songLists.allSongLists[SongListsNames.likedSongsList]

            favoriteSongs?.let {
                if (favoriteSongs.containsKey(songDetails.key)) {
                    slri.favoriteIcon = R.drawable.favorite_filled_24
                }
            }

            arrayList.add(slri)
        }

        _viewState.mviValue {
            it.copy(songLists = arrayList)
        }
    }

    fun playNextSong() {
        musicRepository.playNextSong()
    }

    fun playPrevSong() {
        musicRepository.playPrevSong()
    }

    fun prepareMediaPlayer(position: Int, listName: String) {
        musicRepository.playByPosition(position, listName)

    }

    private fun markSelectedSongUnMarkPrevSong(id: Int) {
        if (id==_viewState.currentState().currentPlayingId) return

        _viewState.mviValue {

            it.copy(
                currentPlayingId = id,
                prevPlayedId = it.currentPlayingId
            )
        }
    }


    fun getMusic() {
        if (_viewState.value!!.currentState.songLists.isEmpty()) {
            disposables.add(musicRepository.getMusic()
                .subscribe({}, { printErrorIfDbg(TAG, it.message.toString()) })
            )

        }
    }

    fun pauseOrPlayMediaPlayer() {
        musicRepository.pauseOrResumeMediaPlayer()
    }

    fun saveOrDeleteFavoriteSong(slri: SongListRecyclerviewItem, index: Int) {

        val tmpArrayList: ArrayList<SongListRecyclerviewItem> = ArrayList()
        tmpArrayList.addAll(_viewState.currentState().copy().songLists)

        disposables.add(musicRepository.checkIfSongIsLiked(slri.song)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (slri.song.listName != SongListsNames.likedSongsList) {
                    changeLikeIconState(tmpArrayList, index)
                } else { // the user is at liked songs list, so,
                    // we need to delete the song from the ui list either
                    deleteSongFromScreen(slri)
                }
            }, { printErrorIfDbg(TAG, it.message) })
        )
    }

    private fun changeLikeIconState(
        tmpArrayList: ArrayList<SongListRecyclerviewItem>,
        index: Int
    ) {

        val item = tmpArrayList[index].copy()
        if (item.favoriteIcon == R.drawable.favorite_filled_24) { //need to remove song's like icon
            item.favoriteIcon = R.drawable.favorite_border_24
        } else {
            item.favoriteIcon = R.drawable.favorite_filled_24
        }
        tmpArrayList[index] = item
        _viewState.mviValue {
            it.copy(
                songLists = tmpArrayList
            )
        }
    }

    private fun deleteSongFromScreen(slri: SongListRecyclerviewItem) {
        val tmpArrayList: ArrayList<SongListRecyclerviewItem> = ArrayList()
        tmpArrayList.addAll(_viewState.currentState().copy().songLists)
        tmpArrayList.remove(slri)
        _viewState.mviValue {
            it.copy(
                songLists = tmpArrayList
            )
        }
    }

    fun deleteSong(slri: SongListRecyclerviewItem) {
        disposables.add(musicRepository.deleteSongFromList(slri.song)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                deleteSongFromScreen(slri)
            }, { printErrorIfDbg(TAG, it.message) })
        )
    }


}