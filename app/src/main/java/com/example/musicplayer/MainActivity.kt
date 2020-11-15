package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.player.AudioPlayer

const val NAV_AUDIO_FILENAME = "NAV_AUDIO_FILENAME"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if(viewModel.audioDataListIsEmpty){
            loadDataFromAssets()
            loadDataFromMediaStore()
        }
        val audioListAdapter = AudioDataRecyclerViewAdapter{
                data, _ -> onAudioDataItemClick(data)
        }
        // listen for changes in audio data list
        viewModel.audioDataList.observe(this, {
            it?.let {
                audioListAdapter.submitList(it)
            }
        })
        binding.recyclerView.adapter = audioListAdapter
    }

    private fun loadDataFromAssets(){
        viewModel.loadAssetAudioData(assets)
        Log.i("MainActivity.Assets", viewModel.audioDataList.value.toString())
    }

    private fun loadDataFromMediaStore(){
        viewModel.loadMediaStoreAudioData(contentResolver)
        Log.i("MainActivity.Assets", viewModel.audioDataList.value.toString())
    }

    private fun onAudioDataItemClick(audioData: AudioData){
        val intent = Intent(this, AudioPlayer::class.java).apply {
            // todo maybe set in view model?
            putExtra(NAV_AUDIO_FILENAME, audioData.fileName)
        }
        startActivity(intent)
    }

}