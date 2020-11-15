package com.example.musicplayer.player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.R
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

        /**
         * find audio to play from main view model based on provided nav arg
         */
        playerViewModel.currentAudioData.observe(viewLifecycleOwner){
            if(it != null){
                if(mediaPlayer?.isPlaying == true){
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                }
                mediaPlayer = MediaPlayer()
                // handle asset audio
                if(it.isAsset){
                    val assetFileDescriptor = requireActivity().assets.openFd("$ASSETS_AUDIO_DIR/${it.fileName}")
                    mediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                } else {
                    // handle media store audio
                    mediaPlayer?.setDataSource(requireContext(), it.uri!!)
                }
                mediaPlayer?.apply {
                    prepare()
                     setVolume(1f, 1f)
                    resume()
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
        // pause/resume
        binding.playerActionBtn.setOnClickListener{
            onPlayerAction()
        }
        // next
        binding.playerNextBtn.setOnClickListener {
            val nextAudio = mainViewModel.nextAudio(playerViewModel.currentAudioData.value!!)!!
            playerViewModel.setCurrentAudio(nextAudio)
        }

        // prev
        binding.playerPrevBtn.setOnClickListener {
            val nextAudio = mainViewModel.prevAudio(playerViewModel.currentAudioData.value!!)!!
            playerViewModel.setCurrentAudio(nextAudio)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
    }

    private fun onPlayerAction(){
        if (playerViewModel.isPlaying.value == true){
            pause()
        } else {
            resume()
        }
    }

    private fun pause(){
        mediaPlayer?.pause()
        playerViewModel.setIsPlaying(false)
    }

    private fun resume(){
        mediaPlayer?.start()
        playerViewModel.setIsPlaying(true)
    }

    private fun releasePlayer(){
        playerViewModel.cleanup()
        if(mediaPlayer?.isPlaying == true){
            mediaPlayer?.stop()
        }
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }
}

@BindingAdapter("playerActionSrc")
fun playerActionSrc(view: ImageButton, isPlaying: Boolean){
    view.setImageResource(if(!isPlaying) R.drawable.ic_baseline_play_circle_outline_24 else R.drawable.ic_baseline_pause_circle_outline_24)
}