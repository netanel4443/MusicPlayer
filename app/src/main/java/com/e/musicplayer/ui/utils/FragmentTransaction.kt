package com.e.musicplayer.ui.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int, tag:String) {
    val currentFragment = supportFragmentManager.findFragmentByTag(tag)
    if (currentFragment == null) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}