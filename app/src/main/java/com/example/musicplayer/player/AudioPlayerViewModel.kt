package com.example.musicplayer.player

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.model.Duration
import kotlinx.coroutines.*

class AudioPlayerViewModel: ViewModel(), LifecycleObserver {
    var mediaPlayer: MediaPlayer? = null
    private val defaultDelayMillis = 200L
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
    get() = _isPlaying
    private val _currentAudioData = MutableLiveData<AudioData?>()
    val currentAudioData: LiveData<AudioData?>
    get() = _currentAudioData

    private val _timer = MutableLiveData<Long>(0L)
    private var timerScope = CoroutineScope(Dispatchers.Main + Job())

    val duration: LiveData<String>
    get() = Transformations.map(_timer) { Duration(it).toString() }
    val durationText: LiveData<String>
        get() = duration.map { "$it : ${currentAudioData.value?.duration}" }

    fun cleanup(){
        _currentAudioData.value = null
        _isPlaying.value = false
    }

    fun setCurrentAudio(audioData: AudioData){
        _currentAudioData.value = audioData
        _timer.value = 0L
    }

    fun setIsPlaying(isPlaying: Boolean){
        if(_isPlaying.value != isPlaying){
            toggleTimer()
        }
        _isPlaying.value = isPlaying
    }

    private fun toggleTimer(){
        if (_isPlaying.value == true){
            // pause timer
            timerScope.cancel()
            timerScope = CoroutineScope(Dispatchers.Main + Job())
        } else {
            // resume timer
            startTimer()
        }
    }

    fun startTimer() = timerScope.launch {
        countUp(_timer.value?:0, currentAudioData.value?.duration?.millis?:0)
    }

    private suspend fun countUp(startMillis: Long, endMillis: Long) {
        // gets behid, maybe try changing context
        _timer.value = startMillis
        for (time in startMillis..endMillis step defaultDelayMillis){
            if(time > startMillis){
                delay(defaultDelayMillis)
            }
            Log.d("MusicPlayer", mediaPlayer?.currentPosition?.toString()?:"0")
            _timer.value = mediaPlayer?.currentPosition?.toLong()?:0
        }
    }

    fun resetTimer(){
        if (timerScope.isActive){
            timerScope.cancel()
            timerScope = CoroutineScope(Dispatchers.Main + Job())
            _timer.value = 0
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyFragment(){
        resetTimer()
        mediaPlayer = null
    }
}