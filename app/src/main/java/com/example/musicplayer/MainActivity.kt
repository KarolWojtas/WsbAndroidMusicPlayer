package com.example.musicplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.example.musicplayer.databinding.ActivityMainBinding


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
    }

    private fun loadDataFromAssets(){
        viewModel.loadAssetAudioData(assets)
        Log.i("MainActivity.Assets", viewModel.audioDataList.value.toString())
    }

    private fun loadDataFromMediaStore(){
        viewModel.loadMediaStoreAudioData(contentResolver)
        Log.i("MainActivity.Assets", viewModel.audioDataList.value.toString())
    }

}