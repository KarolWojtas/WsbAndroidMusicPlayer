package com.example.musicplayer.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.NAV_AUDIO_FILENAME
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityAudioPlayerBinding

class AudioPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var mainViewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player)
        mainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        val audioDataFileName = intent.getStringExtra(NAV_AUDIO_FILENAME)

    }
}