package com.example.musicplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.AudioDataItemBinding
import com.example.musicplayer.model.AudioData

class AudioDataRecyclerViewAdapter: ListAdapter<AudioData, AudioDataRecyclerViewAdapter.ViewHolder>(AudioDataDiffCallback){
    class ViewHolder(private val binding: AudioDataItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(audioData: AudioData){
            binding.audioData = audioData
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: AudioDataItemBinding = AudioDataItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

}

object AudioDataDiffCallback : DiffUtil.ItemCallback<AudioData>() {
    override fun areItemsTheSame(oldItem: AudioData, newItem: AudioData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AudioData, newItem: AudioData): Boolean {
        return oldItem.fileName == newItem.fileName
    }
}