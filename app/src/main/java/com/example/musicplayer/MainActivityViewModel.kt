package com.example.musicplayer

import android.content.ContentResolver
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.services.ASSETS_AUDIO_DIR
import com.example.musicplayer.services.MusicDataService
import java.io.File

class MainActivityViewModel: ViewModel(){
    private val _audioDataList = MutableLiveData<MutableList<AudioData>>(mutableListOf())
    val audioDataList: LiveData<MutableList<AudioData>>
    get() =   _audioDataList
    val audioDataListIsEmpty
    get() = _audioDataList.value == null || (_audioDataList.value != null && _audioDataList.value?.isEmpty()!!)

    fun loadAssetAudioData(assets: AssetManager){
        val audioData = MusicDataService.instance.loadAudioDataFromAssets(assets)
        addToAudioDataList(audioData)
    }

    fun loadMediaStoreAudioData(contentResolver: ContentResolver){
        val audioData = MusicDataService.instance.loadDataFromMediaStore(contentResolver)
        addToAudioDataList(audioData)
    }

    private fun addToAudioDataList(newData: List<AudioData>){
        this._audioDataList.value = this._audioDataList.value?.apply { addAll(newData) }
    }

    fun nextAudio(audioData: AudioData): AudioData?{
        _audioDataList.value?.let {
            val index = it.indexOf(audioData)
            return if(index == it.size - 1) it[index + 1] else it.first()
        }
        return null
    }

    fun prevAudio(audioData: AudioData): AudioData?{
        _audioDataList.value?.let {
            val index = it.indexOf(audioData)
            return if(index > 0) it[index - 1] else it.last()
        }
        return null
    }
}