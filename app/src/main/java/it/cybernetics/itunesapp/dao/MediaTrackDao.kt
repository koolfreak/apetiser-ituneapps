package it.cybernetics.itunesapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface MediaTrackDao {

    /**
     * Inserts the list of Movie Tracks
     */
    @Insert
    fun insert(mediaTracks: List<MediaTrack>)

    /**
     * Get all movie tracks save on the db
     */
    @Query("SELECT * FROM media_tracks")
    fun listAll(): Single<List<MediaTrack>>
}