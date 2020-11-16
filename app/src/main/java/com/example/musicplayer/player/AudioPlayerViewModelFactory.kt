package com.example.musicplayer.player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AudioPlayerViewModelFactory(private val mediaPlayer: MediaPlayer) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)){
            return AudioPlayerViewModel(mediaPlayer) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}