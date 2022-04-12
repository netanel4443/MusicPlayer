package com.e.musicplayer


import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

class SongsPath() {
    fun getAudioFiles(context: Context): Single<HashMap<String,SongDetails>> {

        return Single.fromCallable {


            val audioList = HashMap<String,SongDetails>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST
            )

// Show only audio that are at least 0 minutes in duration.
            val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
            val selectionArgs = arrayOf(
                TimeUnit.MILLISECONDS.convert(0, TimeUnit.MINUTES).toString()
            )

// Display audio in alphabetical order based on their display name.
            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

            val query = context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            query?.use { cursor ->
                // Cache column indices.

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                while (cursor.moveToNext()) {
                    // Get values of columns for a given audio.
                    val id = cursor.getLong(idColumn)
                    var name = cursor.getString(nameColumn)
                    val duration = cursor.getLong(durationColumn)
//                    val artist = cursor.getString(artistColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    val suffix=".mp3"
                    name.removeSuffix(suffix)
                    val lastIndex=name.length
                    val startIndex=lastIndex-suffix.length
                    name=name.removeRange(startIndex,lastIndex)

                    while(audioList.containsKey(name)){
                        name= name.plus("1")
                    }

                    audioList[name] = SongDetails( name, timeFormat(duration),contentUri,
                        id = cursor.position
                    )
                }
            }
            audioList
        }
    }

    private fun timeFormat(millis:Long):String{
        val seconds=millis/1000
        val s: Long = seconds % 60
        val m: Long = seconds / 60 % 60
        val h: Long = seconds / (60 * 60) % 24
       return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        }
        else {
            String.format("%02d:%02d", m, s)
        }
    }
}