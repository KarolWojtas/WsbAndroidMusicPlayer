package com.example.musicplayer.player

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.model.Duration
import kotlinx.coroutines.*

enum class AudioPlayerStates(val toBool: Boolean?) {
    playing(true), paused(false), released(null)

}
class AudioPlayerViewModel: ViewModel(), LifecycleObserver {
    var mediaPlayer: MediaPlayer? = null
    private val defaultDelayMillis = 200L
    private val _status = MutableLiveData(AudioPlayerStates.released)
    val status: LiveData<AudioPlayerStates>
    get() = _status

    private val _currentAudioData = MutableLiveData<AudioData?>()
    val currentAudioData: LiveData<AudioData?>
    get() = _currentAudioData

    private val _timer = MutableLiveData(0L)

    val timer: LiveData<Long>
    get() = _timer

    private var timerScope = CoroutineScope(Dispatchers.Main + Job())

    private val duration: LiveData<String>
    get() = Transformations.map(_timer) { Duration(it).toString() }
    val durationText: LiveData<String>
        get() = duration.map { "$it : ${currentAudioData.value?.duration}" }

    fun cleanup(){
        _currentAudioData.value = null
        _status.value = AudioPlayerStates.released
    }

    fun setCurrentAudio(audioData: AudioData){
        _currentAudioData.value = audioData
        _timer.value = 0L
    }

    fun setIsPlaying(isPlaying: Boolean?){
        if(this._status.value?.toBool != isPlaying){
            toggleTimer()
        }
        setStatusFromBoolean(isPlaying)
    }

    fun toggleTimer(turnOn: Boolean? = null){
        val activateTimer = turnOn ?: (_status.value != AudioPlayerStates.playing)
        if (!activateTimer){
            // pause timer
            timerScope.cancel()
            timerScope = CoroutineScope(Dispatchers.Main + Job())
        } else {
            // resume timer
            startTimer()
        }
    }

    fun startTimer() = timerScope.launch {
        countUp(mediaPlayer?.currentPosition?.toLong()?:0, currentAudioData.value?.duration?.millis?:0)
    }

    private suspend fun countUp(startMillis: Long, endMillis: Long) {
        _timer.value = startMillis
        val currentRange = startMillis..endMillis
        for (time in currentRange step defaultDelayMillis){
            if(time > startMillis){
                delay(defaultDelayMillis)
            }
            _timer.value = when{
                _status.value == AudioPlayerStates.released -> endMillis
                mediaPlayer?.currentPosition?.toLong() in currentRange -> mediaPlayer?.currentPosition?.toLong()
                else -> mediaPlayer?.duration?.toLong()
            }
        }
        _timer.value = endMillis
    }

    fun resetTimer(){
        if (timerScope.isActive){
            timerScope.cancel()
            timerScope = CoroutineScope(Dispatchers.Main + Job())
            _timer.value = 0
        }
    }

    private fun setStatusFromBoolean(value: Boolean?){
        _status.value = when (value) {
            null -> AudioPlayerStates.released
            true -> AudioPlayerStates.playing
            else -> AudioPlayerStates.paused
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyFragment(){
        resetTimer()
    }
}