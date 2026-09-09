package com.speedtest.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SpeedTestEntity::class], version = 1)
abstract class SpeedTestDatabase : RoomDatabase() {
    abstract fun speedTestDao(): SpeedTestDao
    
    companion object {
        @Volatile
        private var instance: SpeedTestDatabase? = null
        
        fun getInstance(context: Context): SpeedTestDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SpeedTestDatabase::class.java,
                    "speed_test_db"
                ).build().also { instance = it }
            }
    }
}
