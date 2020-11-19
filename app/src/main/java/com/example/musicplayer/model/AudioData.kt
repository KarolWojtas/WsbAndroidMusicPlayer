package com.example.musicplayer.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class AudioData constructor(
    val title: String,
    val id: String,
    val uri: Uri? = null,
    val artist: String? = null,
    val duration: Duration? = null,
    val bitRate: Long? = null,
    val mimeType: String? = null,
    val isAsset: Boolean = false
): Parcelable{

    constructor(parcel: Parcel): this(
        title = parcel.readString()?:"",
        id = parcel.readString()?:"",
        uri = Uri.parse(parcel.readString()),
        artist = parcel.readString(),
        duration = Duration(parcel.readLong()),
        bitRate = parcel.readLong(),
        mimeType = parcel.readString(),
        isAsset = parcel.readInt() == 1
    )
    companion object CREATOR: Parcelable.Creator<Any?>  {
        override fun createFromParcel(parcel: Parcel): AudioData? {
            return AudioData(parcel)
        }

        override fun newArray(size: Int): Array<AudioData?> {
            return arrayOfNulls(size)
        }
    }
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(id)
        dest?.writeString(uri?.toString())
        dest?.writeString(artist)
        dest?.writeLong(duration?.millis?:0)
        dest?.writeLong(bitRate?:0)
        dest?.writeString(mimeType);
        dest?.writeInt(if (isAsset) 1 else 0)
    }
}

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
        return "${minutes.toString().padStart(2, '0')}:${secondsMod.toString().padStart(2, '0')}"
    }
}