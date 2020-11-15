package com.example.musicplayer.player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.databinding.FragmentAudioPlayerBinding
import com.example.musicplayer.services.ASSETS_AUDIO_DIR

class AudioPlayerFragment : Fragment() {

    private lateinit var binding: FragmentAudioPlayerBinding
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var playerViewModel: AudioPlayerViewModel
    private var mediaPlayer: MediaPlayer? = null
    val navArgs: AudioPlayerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutInflater = LayoutInflater.from(context)
        binding = FragmentAudioPlayerBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
        playerViewModel = ViewModelProviders.of(requireActivity()).get(AudioPlayerViewModel::class.java)
        binding.mainViewModel = mainViewModel
        binding.playerViewModel = playerViewModel

        playerViewModel.currentAudioData.observe(viewLifecycleOwner){
            if(it != null){
                if(mediaPlayer?.isPlaying == true){
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                }
                mediaPlayer = MediaPlayer()
                if(it.isAsset){
                    val assetFileDescriptor = requireActivity().assets.openFd("$ASSETS_AUDIO_DIR/${it.fileName}")
                    mediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                }
                mediaPlayer?.apply {
                    prepare()
                    setVolume(100f, 100f)
                    start()
                }
            } else {
                mediaPlayer?.stop()
            }
        }
        /**
         * set initial audio from intent
         */
        if(playerViewModel.currentAudioData.value == null){
            mainViewModel.audioDataList.value?.let {
                val fileName = navArgs.audioData.fileName
                // empty - do navigation with fragments
                it.find { audio -> audio.fileName == fileName }?.also {found ->
                    playerViewModel.setCurrentAudio(found)
                }

            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.cleanup()
        if(mediaPlayer?.isPlaying == true){
            mediaPlayer?.stop()
        }
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }
}