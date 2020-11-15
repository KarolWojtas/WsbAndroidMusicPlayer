package com.example.musicplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.AudioDataItemBinding
import com.example.musicplayer.model.AudioData

typealias onClickAudioItemData = (audioData: AudioData, position: Int) -> Unit
class AudioDataRecyclerViewAdapter(private val onClick: onClickAudioItemData): ListAdapter<AudioData, AudioDataRecyclerViewAdapter.ViewHolder>(AudioDataDiffCallback){
    class ViewHolder(private val binding: AudioDataItemBinding, private val onClick: onClickAudioItemData): RecyclerView.ViewHolder(binding.root){
        fun bind(audioData: AudioData, position: Int){
            binding.audioData = audioData
            binding.playBtn.setOnClickListener {
                onClick(audioData, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: AudioDataItemBinding = AudioDataItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, position)
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