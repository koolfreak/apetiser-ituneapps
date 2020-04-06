package it.cybernetics.itunesapp.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Configure Room persistence database to interact with sqlite db
 */
@Database(entities = [MediaTrack::class], version = 1, exportSchema = false)
abstract class DatabaseClient: RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: DatabaseClient? = null

        fun accessDb(context: Context): DatabaseClient =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                DatabaseClient::class.java, "media_track.db")
                .fallbackToDestructiveMigration().build()
    }

    // give access to dao object of Room
    abstract fun mediaTrackDao(): MediaTrackDao
}