package com.example.musicplayer.player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentAudioPlayerBinding
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.services.ASSETS_AUDIO_DIR

class AudioPlayerFragment : Fragment() {

    private lateinit var binding: FragmentAudioPlayerBinding
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var playerViewModel: AudioPlayerViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private val navArgs: AudioPlayerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // init binding
        val layoutInflater = LayoutInflater.from(context)
        binding = FragmentAudioPlayerBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        // init player
        mediaPlayer = MediaPlayer().apply {
            // triggered after prepareAsync is finished
            setOnPreparedListener {
                start()
                playerViewModel.setIsPlaying(true)
            }
            setOnCompletionListener {
                reset()
                playerViewModel.setIsPlaying(false)
            }
        }
        // init view model
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
        playerViewModel = ViewModelProviders.of(requireActivity()).get(AudioPlayerViewModel::class.java)
        binding.mainViewModel = mainViewModel
        binding.playerViewModel = playerViewModel
        viewLifecycleOwner.lifecycle.addObserver(playerViewModel)
        // set new media player in view model on every create
         playerViewModel.mediaPlayer = mediaPlayer
        /**
         * find audio to play from main view model based on provided nav arg
         */
        playerViewModel.currentAudioData.observe(viewLifecycleOwner){
            if(it != null){
                playAudio(it)
            } else {
                mediaPlayer.stop()
            }
        }
        /**
         * set initial audio from intent
         */
        if(playerViewModel.currentAudioData.value == null){
            mainViewModel.audioDataList.value?.let {
                val id = navArgs.audioData.id
                // empty - do navigation with fragments
                it.find { audio -> audio.id == id }?.also { found ->
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

        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                playerViewModel.toggleTimer(false)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mediaPlayer.seekTo(seekBar.progress)
                    playerViewModel.toggleTimer(true)
                }
            }
        })

        playerViewModel.timer.observe(viewLifecycleOwner){
            binding.playerSeekBar.progress = it.toInt()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun onPlayerAction(){
        when{
            playerViewModel.isPlaying.value == true
                -> pause()
            playerViewModel.timer.value == playerViewModel.currentAudioData.value?.duration?.millis
                -> playerViewModel.currentAudioData.value.let {
                if (it != null) {
                    playAudio(it)
                }
            }
            else
                -> resume()
        }
    }

    private fun pause(){
        mediaPlayer.pause()
        playerViewModel.setIsPlaying(false)
    }

    private fun resume(){
        mediaPlayer.start()
        playerViewModel.setIsPlaying(true)
    }

    private fun releasePlayer(){
        playerViewModel.cleanup()
        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun playAudio(audio: AudioData, resetPlayer: Boolean = true){
        if(mediaPlayer.isPlaying){
            playerViewModel.resetTimer()
        }
        if (resetPlayer){
            mediaPlayer.reset()
        }
        // handle asset audio
        if(audio.isAsset){
            val assetFileDescriptor = requireActivity().assets.openFd("$ASSETS_AUDIO_DIR/${audio.id}")
            mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        } else {
            // handle media store audio
            mediaPlayer.setDataSource(requireContext(), audio.uri!!)
        }
        mediaPlayer.apply {
            prepareAsync()
            playerViewModel.startTimer()
        }
        binding.playerSeekBar.apply {
            progress = 0
            max = audio.duration?.millis?.toInt()?:0
        }
    }
}

@BindingAdapter("playerActionSrc")
fun playerActionSrc(view: ImageButton, isPlaying: Boolean){
    view.setImageResource(if(!isPlaying) R.drawable.ic_baseline_play_circle_outline_24 else R.drawable.ic_baseline_pause_circle_outline_24)
}