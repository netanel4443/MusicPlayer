package com.e.musicplayer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.musicplayer.BaseApplication
import com.e.musicplayer.R
import com.e.musicplayer.databinding.MusicAcitivtyBinding
import com.e.musicplayer.di.components.MusicActivityComponent
import com.e.musicplayer.ui.activities.BaseActivity
import com.e.musicplayer.ui.fragments.SongListFragment
import com.e.musicplayer.ui.permissions.PermissionRequestCodes.READ_EXTERNAL_STORAGE_CODE
import com.e.musicplayer.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.musicplayer.ui.recyclerviews.viewholders.CreateAllSongListsViewHolder
import com.e.musicplayer.ui.recyclerviews.viewholders.helpers.GenericItemClickListener
import com.e.musicplayer.ui.services.MusicPlayerService
import com.e.musicplayer.ui.utils.addFragment
import com.e.musicplayer.ui.viewmodels.MusicPlayerViewModel
import com.e.musicplayer.utils.differentItems

class MusicActivity : BaseActivity() {

    private val viewModel: MusicPlayerViewModel by lazy(::getViewModel)
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter<String,CreateAllSongListsViewHolder>
    private lateinit var binding: MusicAcitivtyBinding
    lateinit var musicActivityComponent: MusicActivityComponent
    private val TAG = getTag(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        musicActivityComponent = (application as BaseApplication).appComponent.musicActivityComponent().create()
        musicActivityComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = MusicAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        initUi(binding)
        attachStateObserver()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_CODE
                )
            } else {
                startServiceAndGetSongs()
            }
        }
    }

    private fun startServiceAndGetSongs() {
        viewModel.getMusic()
        startService(Intent(this, MusicPlayerService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startServiceAndGetSongs()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initUi(binding: MusicAcitivtyBinding) {
        initRecyclerView(binding.recyclerview)
        binding.playOrPauseBtn.setOnClickListener {
            viewModel.pauseOrPlayMediaPlayer()
        }
        binding.nextSongBtn.setOnClickListener {
            viewModel.playNextSong()
        }
        binding.prevSongBtn.setOnClickListener {
            viewModel.playPrevSong()
        }


    }

    private fun initRecyclerView(recyclerview: RecyclerView) {
        recyclerviewAdapter =
            GenericRecyclerviewAdapter(R.layout.song_lists_recyclerview_cell_design,
                CreateAllSongListsViewHolder::class.java)

        recyclerviewAdapter.setItemClickListener(object : GenericItemClickListener<String> {
            override fun onItemClick(item: String) {
                addSongListFragment()
                viewModel.createItemsForSongListRecyclerView(item)
            }
        })
        recyclerview.adapter = recyclerviewAdapter
        recyclerview.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )
        recyclerview.setHasFixedSize(true)
    }

    private fun attachStateObserver() {
        viewModel.viewState.observe(this) { state ->
            val prev = state.prevState
            val curr = state.currentState

            when {
                recyclerviewAdapter.hasNoItems() -> {
                    recyclerviewAdapter.addItems(curr.listsName.toList())
                }
                prev.listsName.size < curr.listsName.size -> {
                    val newItems = curr.listsName.differentItems(prev.listsName)
                    recyclerviewAdapter.addItems(newItems)
                }
                prev.listsName.size > curr.listsName.size -> {

                    val itemsToRemove = curr.listsName.differentItems(prev.listsName)
                    recyclerviewAdapter.removeItems(itemsToRemove)
                }
            }

            binding.playOrPauseBtn.setBackgroundResource(curr.playBtnIcon)

        }
    }

    fun addSongListFragment() {
        val fragment = SongListFragment()
        addFragment(fragment, binding.fragmentContainerView.id, "SongListFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}