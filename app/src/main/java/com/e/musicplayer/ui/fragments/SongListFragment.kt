package com.e.musicplayer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.musicplayer.R
import com.e.musicplayer.SongDetails
import com.e.musicplayer.databinding.FragmentSongListBinding
import com.e.musicplayer.ui.MusicActivity
import com.e.musicplayer.ui.recyclerviews.adapters.SongListRecyclerAdapter
import com.e.musicplayer.ui.recyclerviews.items.SongListRecyclerviewItem
import com.e.musicplayer.ui.recyclerviews.viewholders.CreateSongListViewHolder
import com.e.musicplayer.ui.recyclerviews.viewholders.itemclicklisteners.SongItemClickListener
import com.e.musicplayer.ui.viewmodels.MusicPlayerViewModel
import com.e.musicplayer.utils.differentItems
import com.e.musicplayer.utils.oldAndNewItemsPairs
import com.e.musicplayer.utils.printIfDbg

class SongListFragment : BaseSharedVmFragment() {

    private val viewModel: MusicPlayerViewModel by lazy(this::getViewModel)
    private lateinit var binding: FragmentSongListBinding
    private lateinit var songListRecyclerviewAdapter: SongListRecyclerAdapter
    private val TAG = this.javaClass.name

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MusicActivity).musicActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        attachStateObserver()
    }

    private fun initUi() {
        initRecyclerview()

    }

    private fun initRecyclerview() {
        val recyclerview = binding.songsRecyclerview
        songListRecyclerviewAdapter =
            SongListRecyclerAdapter(
                R.layout.song_recyclerview_cell_design,
                CreateSongListViewHolder::class.java
            )

        songListRecyclerviewAdapter.setItemClickListener(object : SongItemClickListener {
            override fun playOrPauseMusic(item: SongDetails, position: Int) {
                viewModel.prepareMediaPlayer(position, item.listName)
            }

            override fun onFavoriteBtnClick(slri: SongListRecyclerviewItem, index:Int) {
                viewModel.saveOrDeleteFavoriteSong(slri,index)
            }

            override fun onSaveSongToList(songDetails: SongDetails) {

            }

            override fun deleteSongFromList(slri: SongListRecyclerviewItem) {
                     viewModel.deleteSong(slri)
            }
        })
        recyclerview.adapter = songListRecyclerviewAdapter
        recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerview.setHasFixedSize(true)
    }

    private fun attachStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            val prev = state.prevState
            val curr = state.currentState

            if (songListRecyclerviewAdapter.hasNoItems()) {
                songListRecyclerviewAdapter.addItems(curr.songLists)
            }
            else if (prev.songLists.size < curr.songLists.size) {
                val newItems = curr.songLists.differentItems(prev.songLists)
                songListRecyclerviewAdapter.addItems(newItems)
            }
            else if (prev.songLists.size > curr.songLists.size) {

                val itemsToRemove = curr.songLists.differentItems(prev.songLists)
                songListRecyclerviewAdapter.removeItems(itemsToRemove)
            }
            else if (prev.songLists.size == curr.songLists.size && curr.songLists != prev.songLists) {
                val differentItems=curr.songLists.oldAndNewItemsPairs(prev.songLists)
                songListRecyclerviewAdapter.changeItems(differentItems)
            }

                if (curr.currentPlayingId > -1 ){
                    songListRecyclerviewAdapter.newSongAnimation(
                        curr.prevPlayedId,
                        curr.currentPlayingId
                    )
                }
        }
    }
}