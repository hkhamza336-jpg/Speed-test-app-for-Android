package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SpeedTestRecord

@Database(entities = [SpeedTestRecord::class], version = 1, exportSchema = false)
abstract class SpeedTestDatabase : RoomDatabase() {
    abstract fun speedTestDao(): SpeedTestDao

    companion object {
        @Volatile
        private var instance: SpeedTestDatabase? = null

        fun getDatabase(context: Context): SpeedTestDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SpeedTestDatabase::class.java,
                    "speed_test_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
