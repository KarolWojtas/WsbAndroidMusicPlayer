package com.example.musicplayer.services

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import com.example.musicplayer.model.AudioData
import com.example.musicplayer.model.Duration
import java.lang.Exception


const val ASSETS_AUDIO_DIR = "audio"

class MusicDataService private constructor() {
    companion object {
        val instance = MusicDataService()

    }

    fun loadAudioDataFromAssets(assets: AssetManager): List<AudioData>{
        val dataList = mutableListOf<AudioData>()
        val fileNames = assets.list(ASSETS_AUDIO_DIR)
        fileNames?.let {
            for (fileName in fileNames){
                val assetDescriptor = assets.openFd("${ASSETS_AUDIO_DIR}/$fileName")
                // try to get file meta data
                val analyzedAudioData = getMetaDataFromFile(fileName = fileName, assetFileDescriptor = assetDescriptor)
                if(analyzedAudioData != null){
                    dataList.add(analyzedAudioData)
                } else {
                    dataList.add(AudioData(title = fileNameWithoutExtension(fileName), uri = null, fileName = fileName, isAsset = true))
                }
                assetDescriptor.close()
            }
        }

        return dataList
    }

    private fun getMetaDataFromFile(fileName: String, assetFileDescriptor: AssetFileDescriptor): AudioData?{
        val mediaMetadataRetriever = MediaMetadataRetriever()
        val audioData: AudioData?
        try {
            // assets are bundled together, so we have to pass start & length of file, so that it can be parsed
            mediaMetadataRetriever.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
            val hasAudio = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
            val title: String? = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist: String? = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            val mime = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            audioData = AudioData(
                    title = title?:fileNameWithoutExtension(fileName),
                    fileName = fileName,
                    artist = artist,
                    duration = Duration(duration.toLong()),
                    bitRate = bitrate.toLongOrNull(),
                    mimeType = mime,
                    isAsset = true
            )
            Log.d("MusicDataService", "file: $fileName, title: $title, hasAudio: $hasAudio")
        } catch (e: Exception){
            Log.e("MusicDataService", e.message?:e.stackTraceToString())
            return null
        } finally {
            mediaMetadataRetriever.release()
        }
        return audioData

    }

    private fun fileNameWithoutExtension(fileName: String): String{
        return fileName.split(".").first()
    }

}