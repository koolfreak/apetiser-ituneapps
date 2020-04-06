package it.cybernetics.itunesapp.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_tracks")
class MediaTrack {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    lateinit var trackName: String
    lateinit var mediaImage: String
    lateinit var primaryGenreName: String
    var collectionPrice: Double = 0.0
    var longDescription: String? = null

}