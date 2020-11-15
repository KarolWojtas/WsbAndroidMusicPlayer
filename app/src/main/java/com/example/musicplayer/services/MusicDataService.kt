package com.example.musicplayer.services

import android.content.ContentResolver
import android.content.ContentUris
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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

    /**
     * get audio files from assets (bundled with APK)
     */
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

    /**
     * Load audio data using content resolver query from media store
     * todo verify on real device - no files found on emulator
     */
    fun loadDataFromMediaStore(contentResolver: ContentResolver): List<AudioData> {
        val audioDataList = mutableListOf<AudioData>()
        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME, // verify what it is
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.MIME_TYPE,
                // MediaStore.Audio.Media.SIZE
        )
        val query = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                "",
                arrayOf(),
                ""
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val mimeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            Log.d("MusicDataService.Media", "count: ${cursor.count}")
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getInt(durationColumn)
                val mime = cursor.getString(mimeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                )
                Log.d("MainActivity", arrayOf(contentUri, name, duration).toString())
                audioDataList.add(AudioData(
                        // todo file name
                        title = title,
                        fileName = id.toString(),
                        artist = artist,
                        duration = Duration(duration.toLong()),
                        bitRate = null,
                        mimeType = mime
                ))
            }
        }
        return audioDataList
    }

}