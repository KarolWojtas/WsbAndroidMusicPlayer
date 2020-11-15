package com.example.musicplayer.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.AudioData

class AudioPlayerViewModel: ViewModel(){
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
    get() = _isPlaying
    private val _currentAudioData = MutableLiveData<AudioData?>()
    val currentAudioData: LiveData<AudioData?>
    get() = _currentAudioData

    fun cleanup(){
        _currentAudioData.value = null
        _isPlaying.value = false
    }

    fun setCurrentAudio(audioData: AudioData){
        _currentAudioData.value = audioData
    }

    fun setIsPlaying(isPlaying: Boolean){
        _isPlaying.value = isPlaying
    }
}