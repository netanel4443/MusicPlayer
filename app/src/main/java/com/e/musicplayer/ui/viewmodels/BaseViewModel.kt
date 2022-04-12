package com.e.musicplayer.ui.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {

    protected val disposables=CompositeDisposable()



    override fun onCleared() {
        super.onCleared()

     disposables.clear()
    }
}