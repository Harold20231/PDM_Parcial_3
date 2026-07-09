package com.example.rankeuca.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.rankeuca.data.database.dao.QuestionDao
import com.example.rankeuca.data.database.dao.OptionDao
import com.example.rankeuca.data.database.entities.QuestionEntity
import com.example.rankeuca.data.database.entities.OptionEntity

@Database(
    entities = [QuestionEntity::class, OptionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun optionDao(): OptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rankeuca_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}