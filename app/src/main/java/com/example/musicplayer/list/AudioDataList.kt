package com.example.musicplayer.list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.musicplayer.MainActivityViewModel
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentAudioDataListBinding
import com.example.musicplayer.model.AudioData

class AudioDataList : Fragment() {

    private lateinit var binding: FragmentAudioDataListBinding
    private lateinit var mainViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutInflater = LayoutInflater.from(context)
        binding = FragmentAudioDataListBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        // get main view model from activity
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
        binding.mainViewModel = mainViewModel
        val audioListAdapter = AudioDataRecyclerViewAdapter{
                data, _ -> onAudioDataItemClick(data)
        }
        // listen for changes in audio data list
        mainViewModel.audioDataList.observe(viewLifecycleOwner, {
            it?.let {
                audioListAdapter.submitList(it)
            }
        })
        binding.recyclerView.adapter = audioListAdapter

        return binding.root
    }

    private fun onAudioDataItemClick(audioData: AudioData){
         findNavController().navigate(AudioDataListDirections.actionAudioDataListToAudioPlayerFragment(audioData))
    }
}