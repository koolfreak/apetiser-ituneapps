package it.cybernetics.itunesapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface MediaTrackDao {

    @Insert
    fun insert(mediaTracks: List<MediaTrack>)

    @Query("SELECT * FROM media_tracks")
    fun listAll(): Single<List<MediaTrack>>
}