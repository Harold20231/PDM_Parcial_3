package com.example.rankeuca.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rankeuca.data.database.entities.OptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OptionDao {
    @Query("SELECT * FROM options WHERE questionId = :questionId")
    fun getOptionsForQuestion(questionId: Int): Flow<List<OptionEntity>>

    @Query("SELECT * FROM options")
    fun getAllOptions(): Flow<List<OptionEntity>>

    @Query("SELECT * FROM options WHERE questionId = :questionId")
    suspend fun getOptionsForQuestionSuspend(questionId: Int): List<OptionEntity>

    @Query("SELECT * FROM options WHERE id = :optionId")
    suspend fun getOptionById(optionId: Int): OptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<OptionEntity>)

    @Query("DELETE FROM options")
    suspend fun deleteAll()
}