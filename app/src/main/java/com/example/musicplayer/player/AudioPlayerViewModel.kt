package com.example.musicplayer.player

import android.media.MediaPlayer
import androidx.lifecycle.*
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.model.Duration
import kotlinx.coroutines.*

class AudioPlayerViewModel(val mediaPlayer: MediaPlayer): ViewModel(){
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

    fun toggleTimer(){
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
        val startMod = startMillis % defaultDelayMillis
        var delay = if(startMod != 0L) startMod else defaultDelayMillis
        for (time in startMillis..endMillis step delay){
            if(time > startMillis){
                delay(delay)
            }
            delay = defaultDelayMillis
            _timer.value = mediaPlayer.currentPosition.toLong()
        }
    }

    fun resetTimer(){
        if (timerScope.isActive){
            timerScope.cancel()
            timerScope = CoroutineScope(Dispatchers.Main + Job())
            _timer.value = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (timerScope.isActive){
            timerScope.cancel()
            _timer.value = 0
        }
    }
}