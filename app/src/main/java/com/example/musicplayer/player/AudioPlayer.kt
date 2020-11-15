package com.example.musicplayer.player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.NAV_AUDIO_FILENAME
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityAudioPlayerBinding
import com.example.musicplayer.services.ASSETS_AUDIO_DIR

class AudioPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var playerViewModel: AudioPlayerViewModel
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player)
        mainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        playerViewModel = ViewModelProviders.of(this).get(AudioPlayerViewModel::class.java)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        playerViewModel.currentAudioData.observe(this){
            if(it != null){
                mediaPlayer?.stop()
                mediaPlayer = MediaPlayer()
                if(it.isAsset){
                    val assetFileDescriptor = assets.openFd("${ASSETS_AUDIO_DIR}/${it.fileName}")
                    mediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                }
                mediaPlayer?.start()

            } else {
                mediaPlayer?.stop()
            }
        }
        /**
         * set initial audio from intent
         */
        if(playerViewModel.currentAudioData.value == null){
            mainViewModel.audioDataList.value?.let {
                val fileName = intent.getStringExtra(NAV_AUDIO_FILENAME)
                // empty - do navigation with fragments
                it.find { audio -> audio.fileName == fileName }?.also {found ->
                    playerViewModel.setCurrentAudio(found)
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.cleanup()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}