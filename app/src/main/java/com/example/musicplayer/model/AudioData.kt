package com.example.musicplayer.model

import android.net.Uri

data class AudioData(val title: String,
                     val fileName: String,
                     val uri: Uri? = null,
                     val artist: String? = null,
                     val duration: Duration? = null,
                     val bitRate: Long? = null,
                     val mimeType: String? = null,
                     val isAsset: Boolean = false
)

data class Duration(val millis: Long = 0){
    val millisMod
    get() = millis % 1000
    val seconds
    get() = millis / 1000
    val secondsMod
    get() = seconds % 60
    val minutes
    get() = seconds / 60
    override fun toString(): String {
        return "${minutes.toString().padStart(2, '0')}:$secondsMod"
    }
}