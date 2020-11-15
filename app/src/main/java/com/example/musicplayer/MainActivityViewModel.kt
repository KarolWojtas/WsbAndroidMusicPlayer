package com.example.musicplayer

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

    fun loadAssetAudioData(assets: AssetManager){
        if(_audioDataList.value == null || (_audioDataList.value != null && _audioDataList.value?.isEmpty()!!)){
            val audioData = MusicDataService.instance.loadAudioDataFromAssets(assets)
            addToAudioDataList(audioData)
        }
    }

    fun addToAudioDataList(newData: List<AudioData>){
        this._audioDataList.value = this._audioDataList.value?.apply { addAll(newData) }
    }
}